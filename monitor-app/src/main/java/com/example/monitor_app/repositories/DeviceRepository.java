package com.example.monitor_app.repositories;

import com.example.monitor_app.entities.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;
import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends JpaRepository<Device, UUID> {

    /**
     * Example: JPA generate Query by Field
     */
    List<Device> findByDescription(String description);
    List<Device> findByUserId(UUID userId);
    /**
     * Example: Write Custom Query
     */
    @Query(value = "SELECT d " +
            "FROM Device d " +
            "WHERE d.description = :description ")
    Optional<Device> findDeviceByName(@Param("description") String description);

    // Find all devices
    List<Device> findAll();


}
