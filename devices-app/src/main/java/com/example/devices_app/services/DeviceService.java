package com.example.devices_app.services;

import com.example.devices_app.controllers.handlers.exceptions.model.ResourceNotFoundException;
import com.example.devices_app.entities.UserAux;
import com.example.devices_app.repositories.UserAuxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Service;
import com.example.devices_app.repositories.DeviceRepository;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.example.devices_app.entities.Device;
import com.example.devices_app.dtos.DeviceDTO;
import com.example.devices_app.dtos.DeviceDetailsDTO;
import com.example.devices_app.dtos.builders.DeviceBuilder;
import org.springframework.web.client.RestTemplate;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

@Service
public class DeviceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);
    private final DeviceRepository deviceRepository;
    private final UserAuxRepository userAuxRepository;

    @Value( "${backend.ip}" )
    private String backendIp;

    @Value( "${backend.port}" )
    private String backendPort;

    @Value("device_queue")
    private String queueName;


    @Autowired
    public DeviceService(DeviceRepository deviceRepository, UserAuxRepository userAuxRepository){

        this.deviceRepository = deviceRepository;
        this.userAuxRepository = userAuxRepository;

    }

    public List<DeviceDTO> findDevices() {
        List<Device> deviceList = deviceRepository.findAll();
        return deviceList.stream()
                .map(DeviceBuilder::toDeviceDTO)
                .collect(Collectors.toList());
    }

    public DeviceDetailsDTO findDeviceById(UUID id) {
        Optional<Device> deviceOptional = deviceRepository.findById(id);
        if (!deviceOptional.isPresent()) {
            LOGGER.error("Device with id {} was not found in db", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }
        return DeviceBuilder.toDeviceDetailsDTO(deviceOptional.get());
    }

//    public UUID insert(DeviceDetailsDTO deviceDTO) {
//        Device device = DeviceBuilder.toEntity(deviceDTO);
//        device = deviceRepository.save(device);
//        LOGGER.debug("Device with id {} was inserted in db", device.getId());
//
//        // Trimitem request către monitor-app pentru a insera dispozitivul
//        String monitorAppUrl = "http://" + backendIp + ":" + backendPort + "/monitor/device/insert";
//        RestTemplate restTemplate = new RestTemplate();
//        restTemplate.postForEntity(monitorAppUrl, deviceDTO, Void.class);
//
//        return device.getId();
//    }


    private void sendToQueue(String operation, DeviceDetailsDTO deviceDTO) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbitmq");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(queueName, true, false, false, null);
            Map<String, Object> message = new HashMap<>();
            message.put("operation", operation);
            message.put("deviceDetails", deviceDTO);
            ObjectMapper objectMapper = new ObjectMapper();
            String messageString = objectMapper.writeValueAsString(message);
            channel.basicPublish("", queueName, null, messageString.getBytes());
            LOGGER.info("Message sent to RabbitMQ for operation: {}", operation);
        } catch (Exception e) {
            LOGGER.error("Failed to send message to RabbitMQ: {}", e.getMessage());
        }
    }



    //Update
    public UUID update(UUID deviceId, DeviceDetailsDTO deviceDTO) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + deviceId));

        device.setDescription(deviceDTO.getDescription());
        device.setAddress(deviceDTO.getAddress());
        device.setmaxHourlyEnergyConsumption(deviceDTO.getmaxHourlyEnergyConsumption());
        device.setUserId(deviceDTO.getUserID());

        deviceRepository.save(device);

        // Synchronize with monitor-app
        sendToQueue("UPDATE", deviceDTO);

        return device.getId();
    }

    //Delete
    public void delete(UUID deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + deviceId));


        // Synchronize with monitor-app
        DeviceDetailsDTO deviceDTO = new DeviceDetailsDTO();
        deviceDTO.setId(device.getId());
        sendToQueue("DELETE", deviceDTO);

        deviceRepository.delete(device);

    }

    public UUID insertDeviceForUser(UUID userId, DeviceDetailsDTO deviceDTO) {
        LOGGER.info("Starting insertDeviceForUser for userId: {}", userId);
        // Verificam daca user_id exista in users_aux
        if (!userAuxRepository.existsById(userId)) {
            LOGGER.error("User ID {} not found in user_aux", userId);
            throw new RuntimeException("User ID not found in user_aux");
        }

        // Conversia DTO la entitate si setarea userId
        Device device = DeviceBuilder.toEntity(deviceDTO);
        device.setUserId(userId);

        LOGGER.info("Saving device with details: {}", device);

        device = deviceRepository.save(device);
        UUID deviceId = device.getId();

        if (deviceId == null) {
            LOGGER.error("Device ID is null after saving");
            throw new RuntimeException("Device ID is null after saving");
        }
        LOGGER.info("Successfully saved device with ID: {}", device.getId());

        // Actualizăm DTO-ul cu ID-ul generat
        deviceDTO.setId(deviceId);

        // Synchronize with monitor-app
        sendToQueue("INSERT", deviceDTO);

        return device.getId();
    }

    public boolean insertUserAux(UUID userId) {
        try {
            UserAux userAux = new UserAux();
            userAux.setId(userId); // Setam doar userId pentru user_aux
            userAuxRepository.save(userAux);
            LOGGER.info("User with id {} was inserted", userId);
            return true; // Inserare reusita
        } catch (Exception e) {
            // Log pentru debugging
            System.out.println("Failed to insert user into users_aux: " + e.getMessage());
            return false; // Inserare esuata
        }
    }

    public boolean deleteUserAux(UUID userId) {
        LOGGER.error("Request to delete userAux id: {}", userId);
        try {
            if (userAuxRepository.existsById(userId)) {
                userAuxRepository.deleteById(userId);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.out.println("Failed to delete user from users_aux: " + e.getMessage());
            return false;
        }
    }

    public List<DeviceDTO> getDevicesByUserId(UUID userId) {
        List<Device> devices = deviceRepository.findByUserId(userId);
        return devices.stream()
                .map(DeviceBuilder::toDeviceDTO)
                .collect(Collectors.toList());
    }


    public void nullifyUserId(UUID userId) {
        LOGGER.info("Nullify request received for userId: {}", userId);

        List<Device> devices = deviceRepository.findByUserId(userId);
        for (Device device : devices) {
            device.setUserId(null);
            deviceRepository.save(device);

            // Synchronize with monitor-app
            DeviceDetailsDTO deviceDTO = DeviceBuilder.toDeviceDetailsDTO(device);
            sendToQueue("UPDATE", deviceDTO);

        }
    }


    public UUID insertDeviceNoUser(DeviceDetailsDTO deviceDTO) {
        Device device = DeviceBuilder.toEntity(deviceDTO);
        device = deviceRepository.save(device);

        // Actualizăm DTO-ul cu ID-ul generat
        deviceDTO.setId(device.getId());

        // Synchronize with monitor-app
        sendToQueue("INSERT", deviceDTO);

        return device.getId();
    }


}
