package com.example.devices_app.dtos.builders;

import com.example.devices_app.entities.Device;
import com.example.devices_app.dtos.DeviceDTO;
import com.example.devices_app.dtos.DeviceDetailsDTO;


public class DeviceBuilder {

    private DeviceBuilder(){

    }


    public static DeviceDTO toDeviceDTO(Device device) {
        return new DeviceDTO(device.getId(), device.getDescription(), device.getAddress(), device.getMax_hourly_energy_consumption(),device.getUserId());
    }

    public static DeviceDetailsDTO toDeviceDetailsDTO(Device device) {
        return new DeviceDetailsDTO(device.getId(), device.getDescription(), device.getAddress(),device.getMax_hourly_energy_consumption(),device.getUserId());
    }

    public static Device toEntity(DeviceDetailsDTO deviceDetailsDTO) {
        return new Device(deviceDetailsDTO.getDescription(),
                deviceDetailsDTO.getAddress(),deviceDetailsDTO.getMax_hourly_energy_consumption(), deviceDetailsDTO.getUserID());
    }

}
