package com.example.usersapp.services;

import com.example.usersapp.controllers.handlers.exceptions.model.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.usersapp.repositories.UserRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.example.usersapp.entities.User;
import com.example.usersapp.dtos.UserDTO;
import com.example.usersapp.dtos.UserDetailsDTO;
import com.example.usersapp.dtos.builders.UserBuilder;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableAsync
@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final RestTemplate restTemplate; //definim RestTemplate

    @Value( "${backend.ip}" )
    private String backendIp;

    @Value( "${backend.port}" )
    private String backendPort;

    @Autowired
    public UserService(UserRepository userRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.restTemplate = new RestTemplate(); // initializam RestTemplate
    }

    public List<UserDTO> findUsers() {
        List<User> userList = userRepository.findAll();
        System.out.println("Users retrieved from DB: " + userList);
        return userList.stream()
                .map(UserBuilder::toUserDTO)
                .collect(Collectors.toList());
    }

    public UserDetailsDTO findUserById(UUID id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            LOGGER.error("User with id {} was not found in db", id);
            throw new ResourceNotFoundException(User.class.getSimpleName() + " with id: " + id);
        }
        return UserBuilder.toUserDetailsDTO(userOptional.get());
    }

    public UUID insert(UserDetailsDTO userDTO) {
        User user = UserBuilder.toEntity(userDTO);
        user = userRepository.save(user);
        LOGGER.debug("User with id {} was inserted in db", user.getId());
        return user.getId();
    }


    //Update
    public UUID update(UUID userId, UserDetailsDTO userDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setName(userDTO.getName());
        user.setRole(userDTO.getRole());

        // Actualizam parola daca este furnizata
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }


        userRepository.save(user);
        return user.getId();
    }

    //Delete
    public void delete(UUID userId, String token) {
        RestTemplate restTemplate = new RestTemplate();

        // Setăm header-ul cu tokenul JWT
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // 1. Setăm valoarea NULL pe user_id din device (devices-app)
        String nullifyUrl = "https://" + backendIp + ":" + backendPort + "/device/nullifyUserId/" + userId;
        LOGGER.info("Sending POST request to {}", nullifyUrl);

        try {
            ResponseEntity<Void> response = restTemplate.exchange(nullifyUrl, HttpMethod.POST, entity, Void.class);
            LOGGER.info("Response from nullifyUserId: {}", response.getStatusCode());
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Eroare la setarea valorii null pentru user_id în devices-db.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Eroare la setarea valorii null pentru user_id în devices-db: " + e.getMessage());
        }

        // 2. Ștergem utilizatorul din baza de date users-db
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        userRepository.delete(user);

        // 3. Apelăm deleteUserAux din devices-app pentru a sincroniza ștergerea în user_aux
        String deleteUrl = "https://" + backendIp + ":" + backendPort + "/device/deleteUserAux/" + userId;
        LOGGER.info("Sending DELETE request to {}", deleteUrl);

        try {
            ResponseEntity<String> response = restTemplate.exchange(deleteUrl, HttpMethod.DELETE, entity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                LOGGER.info("User deleted successfully from users_aux in devices-app.");
            } else {
                LOGGER.warn("User was deleted from user_accounts but not found in users_aux.");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to delete user from users_aux in devices-app: {}", e.getMessage());
            throw new RuntimeException("Failed to synchronize deletion with devices-app.");
        }
    }



    @Autowired
    private PasswordEncoder passwordEncoder;

    public UUID insertUserWithSync(UserDetailsDTO userDTO, String token) {
        // 1. Inserare utilizator în users-db
        User user = UserBuilder.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        user = userRepository.save(user);
        UUID userId = user.getId();

        try {
            // 2. Construim request-ul catre devices-app
            String devicesAppUrl = "http://" + backendIp + ":" + backendPort + "/device/insertUserAux";
            Map<String, UUID> requestBody = Map.of("userId", userId);

            // Transmiterea tokenului existent în header
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // Adăugăm tokenul JWT la header
            if (token != null && !token.startsWith("Bearer ")) {
                token = "Bearer " + token; // Adăugăm prefixul 'Bearer' dacă lipsește
            }
            headers.set("Authorization", token); // Transmit tokenul primit din frontend

            HttpEntity<Map<String, UUID>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Void> response = restTemplate.postForEntity(devicesAppUrl, entity, Void.class);

            // 3. Verificăm răspunsul
            if (response.getStatusCode().is2xxSuccessful()) {
                return userId;
            } else {
                userRepository.deleteById(userId); // Revert în caz de eroare
                throw new RuntimeException("Inserare eșuată în devices-db; revert efectuat în users-db.");
            }
        } catch (Exception e) {
            userRepository.deleteById(userId); // Revert în caz de excepție
            throw new RuntimeException("Eroare la sincronizare: " + e.getMessage());
        }
    }

    @Async
    public CompletableFuture<String> runProducerScript(String deviceId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String command = "python /app/Producer.py " + deviceId;
                ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
                processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
                processBuilder.start();
                return "Script started successfully.";
            } catch (Exception e) {
                return "Error while executing script: " + e.getMessage();
            }
        });
    }


}
