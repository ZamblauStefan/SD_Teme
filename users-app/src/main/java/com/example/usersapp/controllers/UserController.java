package com.example.usersapp.controllers;


import com.example.usersapp.controllers.handlers.exceptions.model.ResourceNotFoundException;
import com.example.usersapp.dtos.UserDTO;
import com.example.usersapp.dtos.UserDetailsDTO;

import com.example.usersapp.entities.LoginRequest;
import com.example.usersapp.entities.User;
import com.example.usersapp.repositories.UserRepository;
import com.example.usersapp.security.JwtTokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.example.usersapp.services.UserService;


import javax.validation.Valid;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.CompletableFuture;


@RestController
@CrossOrigin(origins = "https://frontend.localhost", allowCredentials = "true")
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final UserService userService;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    private final JwtTokenUtil jwtTokenUtil;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);


    @Autowired
    public UserController(UserRepository userRepository, UserService userService, PasswordEncoder passwordEncoder,JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }


    @GetMapping()
    public ResponseEntity<List<UserDTO>> getUsers() {
        LOGGER.info("Request received to fetch all users");
        System.out.println("Endpoint /user called");
        List<UserDTO> dtos = userService.findUsers();
        LOGGER.info("Fetched {} users", dtos.size());
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
    public ResponseEntity<String> deleteUser(@RequestHeader("Authorization") String token,
                                             @PathVariable UUID userId) {
        LOGGER.info("Delete user request received for ID: {}, Token: {}", userId, token);
        try {
            userService.delete(userId, token);
            return ResponseEntity.ok("User deleted successfully.");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete user: " + e.getMessage());
        }
    }



    @PostMapping("/userWithSync")
    public ResponseEntity<UUID> insertUserWithSync(@RequestHeader("Authorization") String token, @RequestBody UserDetailsDTO userDTO) {
        UUID userId = userService.insertUserWithSync(userDTO, token);
        return new ResponseEntity<>(userId, HttpStatus.CREATED);
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Test endpoint is working!");
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        LOGGER.info("Login request received for username: {}", loginRequest.getUsername());
        Optional<User> userOptional = userRepository.findUserByName(loginRequest.getUsername());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                // Cream token-ul JWT
                String token = jwtTokenUtil.generateToken(user.getName());
                LOGGER.info("Token generated: {}", token);

                Map<String, String> response = new HashMap<>();
                response.put("userId", user.getId().toString());
                response.put("role", user.getRole());
                response.put("name", user.getName());
                response.put("token", token);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }

        LOGGER.warn("Invalid credentials for username: {}", loginRequest.getUsername());
        return new ResponseEntity<>("Credentiale incorecte", HttpStatus.UNAUTHORIZED);
    }


    @PostMapping("/run-producer/{deviceId}")
    public ResponseEntity<String> runProducerScript(@PathVariable String deviceId) {
        CompletableFuture.runAsync(() -> {
            try {
                // Comanda pentru a executa scriptul Producer.py cu argumentul deviceId
                String command = "python /app/Producer.py " + deviceId;
                ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
                processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
                processBuilder.start(); // Nu așteptăm să se termine scriptul
            } catch (Exception e) {
                LOGGER.error("Error while executing script: {}", e.getMessage());
            }
        });

        // Returnează imediat pentru a nu bloca răspunsul HTTP
        return new ResponseEntity<>("Script started successfully.", HttpStatus.OK);
    }



}
