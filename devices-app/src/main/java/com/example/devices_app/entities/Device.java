package com.example.devices_app.entities;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;


import jakarta.persistence.Entity;
import java.io.Serializable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import java.util.UUID;

@Entity
public class Device implements Serializable {

    private static final long serialVersionUID = 1L;

    //@UuidGenerator
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "maximum_hourly_energy_consumption", nullable = false)
    private int maxHourlyEnergyConsumption;

    @Column(name ="user_id")
    private UUID userId;

    public Device() {}
    public Device(String description, String address, int maxHourlyEnergyConsumption, UUID userId) {
        this.description = description;
        this.address = address;
        this.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption;
        this.userId = userId;
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

    public int getmaxHourlyEnergyConsumption() {
        return maxHourlyEnergyConsumption;
    }

    public void setmaxHourlyEnergyConsumption(int maxHourlyEnergyConsumption) {
        this.maxHourlyEnergyConsumption = maxHourlyEnergyConsumption;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}
