package com.example.usersapp.services;

import com.example.usersapp.controllers.handlers.exceptions.model.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.usersapp.repositories.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.usersapp.entities.User;
import com.example.usersapp.dtos.UserDTO;
import com.example.usersapp.dtos.UserDetailsDTO;
import com.example.usersapp.dtos.builders.UserBuilder;
import org.springframework.web.client.RestTemplate;

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
    public void delete(UUID userId) {
        //setam valoarea NULL pe user_id din device
        String devicesAppUrl = "http://"+ backendIp + ":" + backendPort +"/device/nullifyUserId/" + userId;
        //String devicesAppUrl = "http://devices-app:8082/device/nullifyUserId/" + userId;
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(devicesAppUrl, null, Void.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Eroare la setarea valorii null pentru user_id în devices-db.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Eroare la setarea valorii null pentru user_id în devices-db: " + e.getMessage());
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        // Stergem utilizatorul din baza de date users-db
        userRepository.delete(user);

        // Facem un apel catre devices-app pentru a sincroniza stergerea in user_aux
        String url = "http://" + backendIp + ":" + backendPort +"/device/deleteUserAux/" + userId;

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, null, String.class);

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

    public UUID insertUserWithSync(UserDetailsDTO userDTO) {
        //1:Inseram utilizatorul in users-db
        User user = UserBuilder.toEntity(userDTO);
        // Criptarea parolei
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        user = userRepository.save(user);
        UUID userId = user.getId();

        try {
            //2:Apelam metoda insertUserAux din devices-app
            String devicesAppUrl = "http://"+ backendIp + ":" + backendPort + "/device/insertUserAux"; // Endpoint-ul din devices-app // pentru acces din exterior
            //String devicesAppUrl = "http://devices-app:8082/device/insertUserAux"; //pt acces din docker tot in docker de la users-app la devices-app
            Map<String, UUID> requestBody = Map.of("userId", userId); // userId ca JSON

            ResponseEntity<Void> response = restTemplate.postForEntity(devicesAppUrl, requestBody, Void.class);

            // if(inserarea in users_aux a avut succes) then return userId
            if (response.getStatusCode().is2xxSuccessful()) {
                return userId;
            } else {
                // 3: if fail then se face revert
                userRepository.deleteById(userId);
                throw new RuntimeException("Inserare esuata in devices-db; revert efectuat in users-db.");
            }
        } catch (Exception e) {
            // Se face revert in caz de eroare
            userRepository.deleteById(userId);
            throw new RuntimeException("Eroare la sincronizare: " + e.getMessage());
        }
    }

}
