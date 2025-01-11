package com.example.monitor_app.controllers;

import com.example.monitor_app.entities.EnergyMeasurement;
import com.example.monitor_app.services.EnergyMeasurementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/monitor")
public class MonitorController {

    @Autowired
    private EnergyMeasurementService energyMeasurementService;

    @GetMapping("/all-energy-measurements")
    public List<EnergyMeasurement> getAllEnergyMeasurements() {
        return energyMeasurementService.getAllMeasurements();
    }
}
