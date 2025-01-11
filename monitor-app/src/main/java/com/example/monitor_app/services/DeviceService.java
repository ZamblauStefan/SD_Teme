package com.example.monitor_app.services;

import com.example.monitor_app.controllers.handlers.exceptions.model.ResourceNotFoundException;
import com.example.monitor_app.entities.EnergyMeasurement;
import com.example.monitor_app.entities.UserAux;
import com.example.monitor_app.repositories.UserAuxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.monitor_app.repositories.DeviceRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.monitor_app.entities.Device;
import com.example.monitor_app.dtos.DeviceDTO;
import com.example.monitor_app.dtos.DeviceDetailsDTO;
import com.example.monitor_app.dtos.builders.DeviceBuilder;
import org.springframework.web.bind.annotation.*;

@Service
public class DeviceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);
    private final DeviceRepository deviceRepository;


    @Autowired
    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
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

        // Setăm manual ID-ul dispozitivului din DeviceDetailsDTO
        if (deviceDTO.getId() != null) {
            device.setId(deviceDTO.getId());
        } else {
            throw new IllegalArgumentException("Device ID must be provided for insert operation.");
        }


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
        device.setmaxHourlyEnergyConsumption(deviceDTO.getmaxHourlyEnergyConsumption());
        device.setUserId(deviceDTO.getUserID());

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


    // Metodă pentru a șterge toate înregistrările din devices_table
    public void deleteAllDevices() {
        deviceRepository.deleteAll();
    }


}
