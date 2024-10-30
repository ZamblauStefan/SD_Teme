package com.example.usersapp.dtos;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

public class UserDetailsDTO {

    private UUID id;
    @NotNull
    private String name;
    @NotNull
    private String role;
    @NotNull
    private String password;


    public UserDetailsDTO() {
    }

    public UserDetailsDTO( String name,  String role, String password) {
        this.name = name;
        this.role = role;
        this.password = password;
    }

    public UserDetailsDTO(UUID id, String name, String role, String password) {
        this.id = id;
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
