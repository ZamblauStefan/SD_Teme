package com.example.usersapp.entities;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;


import jakarta.persistence.Entity;
import java.io.Serializable;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;


@Entity
@Table(name = "user_accounts",uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class User implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    //@UuidGenerator
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "role", nullable = false)
    private String role;


    @Column(name = "password", nullable = false)
    private String password;


    public User() {
    }

    public User(String name, String role, String password) {
        this.name = name;
        this.role = role;
        this.password = password;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
