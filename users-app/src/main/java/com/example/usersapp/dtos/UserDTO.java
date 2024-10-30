package com.example.usersapp.dtos;

import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

public class UserDTO extends RepresentationModel<UserDTO>{

    private UUID id;
    private String name;
    private String role;
    private String password;

    UserDTO(){}


    public UserDTO(UUID id, String name, String role, String password) {
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
