package com.example.monitor_app.repositories;

import com.example.monitor_app.entities.UserAux;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.Optional;

public interface UserAuxRepository extends JpaRepository<UserAux, UUID>{


    Optional<UserAux> findById(UUID id);


}
