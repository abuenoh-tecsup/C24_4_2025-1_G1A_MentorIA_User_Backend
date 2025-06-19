package com.tecsup.demo.authentication.controller;

import com.tecsup.demo.authentication.dto.UserDTO;
import com.tecsup.demo.authentication.model.User;
import com.tecsup.demo.authentication.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> crearUser(@RequestBody UserDTO dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Ya existe un usuario con el username: " + dto.getUsername()));
        }

        if (userRepository.existsByEmail(dto.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Ya existe un usuario con el email: " + dto.getEmail()));
        }

        User user = new User();
        user.setPassword(dto.getPassword()); // ⚠️ En producción, usar BCrypt o similar para encriptar
        user.setUsername(dto.getUsername());
        user.setIsStaff(dto.getIsStaff());
        user.setIsActive(dto.getIsActive());
        user.setDateJoined(LocalDateTime.now());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setProfilePictureUrl(dto.getProfilePictureUrl());
        user.setRole(dto.getRole());
        user.setIsSuperuser(false);

        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Usuario creado correctamente"));
    }

    @GetMapping
    public ResponseEntity<List<User>> listarUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerUser(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Usuario no encontrado"));
        }
        return ResponseEntity.ok(userOpt.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUser(@PathVariable Long id, @RequestBody UserDTO dto) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Usuario no encontrado"));
        }

        User user = userOpt.get();

        if (!user.getUsername().equals(dto.getUsername()) &&
                userRepository.existsByUsername(dto.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Ya existe un usuario con el username: " + dto.getUsername()));
        }

        if (!user.getEmail().equals(dto.getEmail()) &&
                userRepository.existsByEmail(dto.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Ya existe un usuario con el email: " + dto.getEmail()));
        }

        user.setPassword(dto.getPassword()); // ⚠️ En producción, hash the password
        user.setUsername(dto.getUsername());
        user.setIsStaff(dto.getIsStaff());
        user.setIsActive(dto.getIsActive());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setProfilePictureUrl(dto.getProfilePictureUrl());
        user.setRole(dto.getRole());

        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Usuario actualizado correctamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Usuario no encontrado"));
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Usuario eliminado correctamente"));
    }
}
