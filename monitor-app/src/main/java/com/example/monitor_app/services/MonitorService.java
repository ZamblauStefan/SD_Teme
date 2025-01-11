package com.example.monitor_app.services;

import com.example.monitor_app.entities.EnergyMeasurement;
import com.example.monitor_app.repositories.EnergyMeasurementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MonitorService {

    @Autowired
    private EnergyMeasurementRepository energyMeasurementRepository;

    public void saveMeasurement(long timestamp, UUID deviceId, double hourlyValue) {
        EnergyMeasurement measurement = new EnergyMeasurement();
        measurement.setTimestamp(timestamp);
        measurement.setDeviceId(deviceId);
        measurement.setHourlyValue(hourlyValue);

        energyMeasurementRepository.save(measurement);
        System.out.println(" [x] Saved measurement: " + hourlyValue + " for device " + deviceId);
    }
}