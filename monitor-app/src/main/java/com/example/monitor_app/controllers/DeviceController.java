package com.example.monitor_app.controllers;

import com.example.monitor_app.dtos.DeviceDTO;
import com.example.monitor_app.dtos.DeviceDetailsDTO;

import org.springframework.hateoas.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.example.monitor_app.services.DeviceService;


import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;

//import pentru verificari
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
//@CrossOrigin(origins = {"http://localhost:3000", "http://react-demo-master:3000", "https://react-demo-master:3000","http://localhost:3000","http://frontend.localhost","https://frontend.localhost"}, allowCredentials = "true")
@RequestMapping(value = "/device")
public class DeviceController {

    private final DeviceService deviceService;
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceController.class);


    @Autowired
    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }


    @PostMapping("/insert")
    public ResponseEntity<Void> insertDevice(@RequestBody DeviceDetailsDTO deviceDTO) {
        deviceService.insert(deviceDTO);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updateDevice(@PathVariable UUID id, @RequestBody DeviceDetailsDTO deviceDTO) {
        deviceService.update(id, deviceDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable UUID id) {
        deviceService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<DeviceDTO>> getAllDevices() {
        List<DeviceDTO> devices = deviceService.findDevices();
        return ResponseEntity.ok(devices);
    }


    @DeleteMapping("/deleteAll")
    public ResponseEntity<Void> deleteAllDevices() {
        deviceService.deleteAllDevices();
        return ResponseEntity.ok().build();
    }


}

