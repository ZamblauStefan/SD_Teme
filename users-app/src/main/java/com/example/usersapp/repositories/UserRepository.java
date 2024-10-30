package com.example.usersapp.repositories;

import com.example.usersapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Example: JPA generate Query by Field
     */
    List<User> findByName(String name);

    /**
     * Example: Write Custom Query
     */
    @Query(value = "SELECT u " +
            "FROM User u " +
            "WHERE u.name = :name ")
    Optional<User> findUserByName(@Param("name") String name);

}
