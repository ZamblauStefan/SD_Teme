package com.example.devices_app.controllers;

import com.example.devices_app.dtos.DeviceDTO;
import com.example.devices_app.dtos.DeviceDetailsDTO;

import org.springframework.hateoas.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.example.devices_app.services.DeviceService;


import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//import pentru verificari
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping(value = "/device")
public class DeviceController {

    private final DeviceService deviceService;
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceController.class);


    @Autowired
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping()
    public ResponseEntity<List<DeviceDTO>> getDevices() {
        List<DeviceDTO> dtos = deviceService.findDevices();
        for (DeviceDTO dto : dtos) {
            Link deviceLink = linkTo(methodOn(DeviceController.class)
                    .getDevice(dto.getId())).withRel("deviceDetails");
            dto.add(deviceLink);
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }


    @PostMapping("/insertDeviceForUser")
    public ResponseEntity<UUID> insertDeviceForUser(@RequestBody DeviceDetailsDTO deviceDTO) {
        UUID deviceID = deviceService.insertDeviceForUser(deviceDTO.getUserID(), deviceDTO);
        return new ResponseEntity<>(deviceID, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<DeviceDetailsDTO> getDevice(@PathVariable("id") UUID deviceId) {
        DeviceDetailsDTO dto = deviceService.findDeviceById(deviceId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    //TODO: UPDATE, DELETE per resource

    @PutMapping(value = "/{id}")
    public ResponseEntity<UUID> updateDevice(@PathVariable("id") UUID deviceId,
                                             @Valid @RequestBody DeviceDetailsDTO deviceDTO) {
        UUID updatedDeviceId = deviceService.update(deviceId, deviceDTO);
        return new ResponseEntity<>(updatedDeviceId, HttpStatus.OK);
    }


    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable("id") UUID deviceId) {
        deviceService.delete(deviceId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/insertUserAux")
    public ResponseEntity<String> insertUserAux(@RequestBody Map<String, UUID> requestBody) {
        UUID userId = requestBody.get("userId");
        boolean isInserted = deviceService.insertUserAux(userId);

        if (isInserted) {
            return ResponseEntity.ok("User inserted successfully in users_aux.");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to insert user in users_aux.");
        }
    }

    @DeleteMapping("/deleteUserAux/{userId}")
    public ResponseEntity<String> deleteUserAux(@PathVariable UUID userId) {
        boolean isDeleted = deviceService.deleteUserAux(userId);

        if (isDeleted) {
            return ResponseEntity.ok("User deleted successfully from users_aux.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found in users_aux.");
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DeviceDTO>> getDevicesByUserId(@PathVariable UUID userId) {
        List<DeviceDTO> devices = deviceService.getDevicesByUserId(userId);
        if (devices != null && !devices.isEmpty()) {
            LOGGER.info("Devices found: " + devices);
            return new ResponseEntity<>(devices, HttpStatus.OK);
        } else {
            LOGGER.info("No devices found for userId: " + userId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping("/nullifyUserId/{userId}")
    public ResponseEntity<Void> nullifyUserId(@PathVariable UUID userId) {
        try {
            deviceService.nullifyUserId(userId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/insertDeviceNoUser")
    public ResponseEntity<UUID> insertDevice(@RequestBody DeviceDetailsDTO deviceDTO) {
        UUID deviceID = deviceService.insertDeviceNoUser(deviceDTO);
        return new ResponseEntity<>(deviceID, HttpStatus.CREATED);
    }



}
