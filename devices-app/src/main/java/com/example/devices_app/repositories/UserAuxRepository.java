package com.example.devices_app.repositories;

import com.example.devices_app.entities.UserAux;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;
import java.util.List;
import java.util.Optional;

public interface UserAuxRepository extends JpaRepository<UserAux, UUID>{


    Optional<UserAux> findById(UUID id);


}
