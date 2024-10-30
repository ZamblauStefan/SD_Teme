package com.example.devices_app.dtos;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class DeviceDetailsDTO {

    private UUID id;
    @NotNull
    private String description;
    @NotNull
    private String address;
    @NotNull
    private int max_hourly_energy_consumption;
    @NotNull
    private UUID userID;

    public DeviceDetailsDTO() {
    }

    public DeviceDetailsDTO(String description, String address, int max_hourly_energy_consumption, UUID userID) {
        this.description = description;
        this.address = address;
        this.max_hourly_energy_consumption = max_hourly_energy_consumption;
        this.userID = userID;
    }

    public DeviceDetailsDTO(UUID id, String description, String address, int max_hourly_energy_consumption, UUID userID) {
        this.id = id;
        this.description = description;
        this.address = address;
        this.max_hourly_energy_consumption = max_hourly_energy_consumption;
        this.userID = userID;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getMax_hourly_energy_consumption() {
        return max_hourly_energy_consumption;
    }

    public void setMax_hourly_energy_consumption(int max_hourly_energy_consumption) {
        this.max_hourly_energy_consumption = max_hourly_energy_consumption;
    }

    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

}
