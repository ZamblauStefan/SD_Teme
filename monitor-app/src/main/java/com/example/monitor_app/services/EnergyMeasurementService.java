package com.example.monitor_app.services;

import com.example.monitor_app.entities.EnergyMeasurement;
import com.example.monitor_app.repositories.EnergyMeasurementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EnergyMeasurementService {

    private final EnergyMeasurementRepository energyMeasurementRepository;

    @Autowired
    public EnergyMeasurementService(EnergyMeasurementRepository energyMeasurementRepository) {
        this.energyMeasurementRepository = energyMeasurementRepository;
    }

    public void saveMeasurement(EnergyMeasurement energyMeasurement) {
        energyMeasurementRepository.save(energyMeasurement);
    }

    public List<EnergyMeasurement> getAllMeasurements() {
        return energyMeasurementRepository.findAll();
    }

    // Metodă pentru a șterge toate înregistrările din EnergyMeasurement
    public void deleteAllMeasurements() {
        energyMeasurementRepository.deleteAll();
    }

}
