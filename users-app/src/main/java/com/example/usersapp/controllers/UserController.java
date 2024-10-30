package com.example.usersapp.controllers;


import com.example.usersapp.controllers.handlers.exceptions.model.ResourceNotFoundException;
import com.example.usersapp.dtos.UserDTO;
import com.example.usersapp.dtos.UserDetailsDTO;

import com.example.usersapp.entities.LoginRequest;
import com.example.usersapp.entities.User;
import com.example.usersapp.repositories.UserRepository;
import org.springframework.hateoas.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.example.usersapp.services.UserService;


import javax.validation.Valid;
import java.util.*;


@RestController
@CrossOrigin
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final UserService userService;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, UserService userService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }


    @GetMapping()
    public ResponseEntity<List<UserDTO>> getUsers() {
        List<UserDTO> dtos = userService.findUsers();
        for (UserDTO dto : dtos) {
            Link userLink = linkTo(methodOn(UserController.class)
                    .getUser(dto.getId())).withRel("userDetails");
            dto.add(userLink);
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }


    @PostMapping()
    public ResponseEntity<UUID> insertUser(@Valid @RequestBody UserDetailsDTO userDTO) {
        UUID userID = userService.insert(userDTO);
        return new ResponseEntity<>(userID, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDetailsDTO> getUser(@PathVariable("id") UUID userId) {
        UserDetailsDTO dto = userService.findUserById(userId);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    //TODO: UPDATE, DELETE per resource

    @PutMapping(value = "/{id}")
    public ResponseEntity<UUID> updateUser(@PathVariable("id") UUID userId,
                                           @Valid @RequestBody UserDetailsDTO userDTO) {
        UUID updatedUserId = userService.update(userId, userDTO);
        return new ResponseEntity<>(updatedUserId, HttpStatus.OK);
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable UUID userId) {
        try {
            userService.delete(userId);
            return ResponseEntity.ok("User deleted successfully.");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete user.");
        }
    }


    @PostMapping("/userWithSync")
    public ResponseEntity<UUID> insertUserWithSync(@RequestBody UserDetailsDTO userDTO) {
        UUID userId = userService.insertUserWithSync(userDTO);
        return new ResponseEntity<>(userId, HttpStatus.CREATED);
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Test endpoint is working!");
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findUserByName(loginRequest.getUsername());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                // Cream un obiect de raspuns care contine rolul
                Map<String, String> response = new HashMap<>();
                response.put("userId", user.getId().toString()); // adaugam ID-ul user-ului
                response.put("role", user.getRole()); //adaugam rolul user-ului
                response.put("name", user.getName()); //adaugam numele user-ului
                response.put("token", "dummy-token"); // fake token, se poate schimba cu un token real
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>("Credentiale incorecte", HttpStatus.UNAUTHORIZED);
    }




}
