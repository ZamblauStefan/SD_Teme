package com.example.usersapp.dtos.builders;

import com.example.usersapp.entities.User;
import com.example.usersapp.dtos.UserDTO;
import com.example.usersapp.dtos.UserDetailsDTO;

public class UserBuilder {

    private UserBuilder(){

    }


    public static UserDTO toUserDTO(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getRole(), user.getPassword());
    }

    public static UserDetailsDTO toUserDetailsDTO(User user) {
        return new UserDetailsDTO(user.getId(), user.getName(), user.getRole(), user.getPassword());
    }

    public static User toEntity(UserDetailsDTO userDetailsDTO) {
        return new User(userDetailsDTO.getName(),
                userDetailsDTO.getRole(),
                userDetailsDTO.getPassword());
    }



}
