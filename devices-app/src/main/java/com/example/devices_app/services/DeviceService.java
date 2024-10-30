package com.example.devices_app.services;

import com.example.devices_app.controllers.handlers.exceptions.model.ResourceNotFoundException;
import com.example.devices_app.entities.UserAux;
import com.example.devices_app.repositories.UserAuxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.devices_app.repositories.DeviceRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.devices_app.entities.Device;
import com.example.devices_app.dtos.DeviceDTO;
import com.example.devices_app.dtos.DeviceDetailsDTO;
import com.example.devices_app.dtos.builders.DeviceBuilder;

@Service
public class DeviceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);
    private final DeviceRepository deviceRepository;
    private final UserAuxRepository userAuxRepository;


    @Autowired
    public DeviceService(DeviceRepository deviceRepository, UserAuxRepository userAuxRepository) {

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

    public UUID insert(DeviceDetailsDTO deviceDTO) {
        Device device = DeviceBuilder.toEntity(deviceDTO);
        device = deviceRepository.save(device);
        LOGGER.debug("Device with id {} was inserted in db", device.getId());
        return device.getId();
    }


    //Update
    public UUID update(UUID deviceId, DeviceDetailsDTO deviceDTO) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + deviceId));

        device.setDescription(deviceDTO.getDescription());
        device.setAddress(deviceDTO.getAddress());
        device.setMax_hourly_energy_consumption(deviceDTO.getMax_hourly_energy_consumption());

        deviceRepository.save(device);
        return device.getId();
    }

    //Delete
    public void delete(UUID deviceId) {
        Device device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found with id: " + deviceId));

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

        return device.getId();
    }

    public boolean insertUserAux(UUID userId) {
        try {
            UserAux userAux = new UserAux();
            userAux.setId(userId); // Setam doar userId pentru user_aux
            userAuxRepository.save(userAux);
            return true; // Inserare reusita
        } catch (Exception e) {
            // Log pentru debugging
            System.out.println("Failed to insert user into users_aux: " + e.getMessage());
            return false; // Inserare esuata
        }
    }

    public boolean deleteUserAux(UUID userId) {
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

}
