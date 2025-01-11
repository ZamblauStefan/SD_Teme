package com.example.monitor_app.entities;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class EnergyMeasurementId implements Serializable {
    private long timestamp;
    private UUID deviceId;

    public EnergyMeasurementId() {}

    public EnergyMeasurementId(long timestamp, UUID deviceId) {
        this.timestamp = timestamp;
        this.deviceId = deviceId;
    }

    // Getters, setters, equals, și hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnergyMeasurementId that = (EnergyMeasurementId) o;
        return timestamp == that.timestamp && Objects.equals(deviceId, that.deviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, deviceId);
    }
}
