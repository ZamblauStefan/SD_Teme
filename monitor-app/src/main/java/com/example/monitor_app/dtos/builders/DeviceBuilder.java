package com.example.monitor_app.dtos.builders;

import com.example.monitor_app.entities.Device;
import com.example.monitor_app.dtos.DeviceDTO;
import com.example.monitor_app.dtos.DeviceDetailsDTO;


public class DeviceBuilder {

    private DeviceBuilder(){

    }


    public static DeviceDTO toDeviceDTO(Device device) {
        return new DeviceDTO(device.getId(), device.getDescription(), device.getAddress(), device.getmaxHourlyEnergyConsumption(),device.getUserId());
    }

    public static DeviceDetailsDTO toDeviceDetailsDTO(Device device) {
        return new DeviceDetailsDTO(device.getId(), device.getDescription(), device.getAddress(),device.getmaxHourlyEnergyConsumption(),device.getUserId());
    }

    public static Device toEntity(DeviceDetailsDTO deviceDetailsDTO) {
        Device device = new Device();
        device.setId(deviceDetailsDTO.getId());
        device.setDescription(deviceDetailsDTO.getDescription());
        device.setAddress(deviceDetailsDTO.getAddress());
        device.setmaxHourlyEnergyConsumption(deviceDetailsDTO.getmaxHourlyEnergyConsumption());
        device.setUserId(deviceDetailsDTO.getUserID());
        return device;
    }

}
