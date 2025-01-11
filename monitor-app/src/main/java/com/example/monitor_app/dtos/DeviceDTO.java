package com.example.monitor_app.dtos;

import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

public class DeviceDTO extends RepresentationModel<DeviceDTO>{

    private UUID id;
    private String description;
    private String address;
    private int maxHourlyEnergyConsumption;
    private UUID userID;

    DeviceDTO(){}

    public DeviceDTO(UUID id, String description, String address, int maxHourlyEnergyConsumption, UUID userID) {
        this.id = id;
        this.description = description;
        this.address = address;
        this.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption;
        this.userID = userID;
    }

    public DeviceDTO(String description, String address, int maxHourlyEnergyConsumption, UUID userID) {
        this.description = description;
        this.address = address;
        this.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption;
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

    public int getMaxHourlyEnergyConsumption() {
        return maxHourlyEnergyConsumption;
    }

    public void setMaxHourlyEnergyConsumption(int maxHourlyEnergyConsumption) {
        this.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption;
    }

    public UUID getUserID() {
        return userID;
    }

    public void setUserID(UUID userID) {
        this.userID = userID;
    }

}
