package com.example.monitor_app.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "energymeasurement")
@IdClass(EnergyMeasurementId.class)
public class EnergyMeasurement {

    @Id
    @Column(name = "timestamp", nullable = false)
    private long timestamp;  // Timpul în milisecunde, utilizat ca parte din primary key

    @Id
    @Column(name = "device_id", nullable = false)
    private UUID deviceId;  // UUID-ul dispozitivului, utilizat ca parte din primary key

    @Id
    @Column(name = "hourly_value", nullable = true)
    private double hourlyValue;  // Valoarea măsurată

    // Getters și setters
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public UUID getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
        this.deviceId = deviceId;
    }

    public double getHourlyValue() {
        return hourlyValue;
    }

    public void setHourlyValue(double hourlyValue) {
        this.hourlyValue = hourlyValue;
    }
}
