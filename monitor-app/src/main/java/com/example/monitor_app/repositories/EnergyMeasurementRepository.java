package com.example.monitor_app.repositories;

import com.example.monitor_app.entities.EnergyMeasurement;
import com.example.monitor_app.entities.EnergyMeasurementId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface EnergyMeasurementRepository extends JpaRepository<EnergyMeasurement, EnergyMeasurementId> {

    List<EnergyMeasurement> findByDeviceIdAndTimestampBetween(UUID deviceId, long startTimestamp, long endTimestamp);
    List<EnergyMeasurement> findByDeviceId(UUID deviceId);
}
