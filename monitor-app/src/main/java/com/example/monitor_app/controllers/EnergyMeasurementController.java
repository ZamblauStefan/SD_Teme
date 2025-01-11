package com.example.monitor_app.controllers;

import com.example.monitor_app.entities.EnergyMeasurement;
import com.example.monitor_app.repositories.EnergyMeasurementRepository;
import com.example.monitor_app.services.EnergyMeasurementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(value = "/energy")
public class EnergyMeasurementController {

    @Autowired
    private EnergyMeasurementRepository energyMeasurementRepository;
    private final EnergyMeasurementService energyMeasurementService;

    @Autowired
    public EnergyMeasurementController(EnergyMeasurementService energyMeasurementService) {
        this.energyMeasurementService = energyMeasurementService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<EnergyMeasurement>> getAllMeasurements() {
        List<EnergyMeasurement> measurements = energyMeasurementService.getAllMeasurements();
        return ResponseEntity.ok(measurements);
    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<Void> deleteAllMeasurements() {
        energyMeasurementService.deleteAllMeasurements();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/getDeviceMeasurementsWithDate/{deviceId}")
    public ResponseEntity<List<EnergyMeasurement>> getDeviceMeasurements(
            @PathVariable UUID deviceId,
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        // Dacă data nu este specificată, folosim ziua curentă
        if (date == null) {
            date = LocalDate.now();
        }

        // Convertim data selectată în interval de timp pentru ziua respectivă
        Date startDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        // Obținem măsurătorile dispozitivului pentru ziua respectivă
        List<EnergyMeasurement> measurements = energyMeasurementRepository.findByDeviceIdAndTimestampBetween(deviceId, startDate.getTime(), endDate.getTime());

        if (measurements.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(measurements);
    }

    @GetMapping("/getDeviceMeasurements/{deviceId}")
    public ResponseEntity<List<EnergyMeasurement>> getDeviceMeasurements(@PathVariable UUID deviceId) {
        List<EnergyMeasurement> measurements = energyMeasurementRepository.findByDeviceId(deviceId);
        if (measurements.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(measurements);
    }

}
