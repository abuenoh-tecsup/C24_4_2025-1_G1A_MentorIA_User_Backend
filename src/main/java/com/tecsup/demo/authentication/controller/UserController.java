package com.tecsup.demo.authentication.controller;

import com.tecsup.demo.authentication.dto.UserDTO;
import com.tecsup.demo.authentication.model.User;
import com.tecsup.demo.authentication.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public String crearUser(@RequestBody UserDTO dto) {
        // Verificar si ya existe un usuario con el mismo username
        if (userRepository.existsByUsername(dto.getUsername())) {
            return "Ya existe un usuario con el username: " + dto.getUsername();
        }

        // Verificar si ya existe un usuario con el mismo email
        if (userRepository.existsByEmail(dto.getEmail())) {
            return "Ya existe un usuario con el email: " + dto.getEmail();
        }

        User user = new User();
        user.setPassword(dto.getPassword()); // En producción, encriptar la contraseña
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
        return "Usuario creado correctamente";
    }

    @GetMapping
    public List<User> listarUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public User obtenerUser(@PathVariable Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public String actualizarUser(@PathVariable Long id, @RequestBody UserDTO dto) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return "Usuario no encontrado";
        }

        User user = userOpt.get();

        // Verificar si el nuevo username ya existe (y no es el mismo usuario)
        if (!user.getUsername().equals(dto.getUsername()) &&
                userRepository.existsByUsername(dto.getUsername())) {
            return "Ya existe un usuario con el username: " + dto.getUsername();
        }

        // Verificar si el nuevo email ya existe (y no es el mismo usuario)
        if (!user.getEmail().equals(dto.getEmail()) &&
                userRepository.existsByEmail(dto.getEmail())) {
            return "Ya existe un usuario con el email: " + dto.getEmail();
        }

        user.setPassword(dto.getPassword()); // En producción, encriptar la contraseña
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
        return "Usuario actualizado correctamente";
    }

    @DeleteMapping("/{id}")
    public String eliminarUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return "Usuario eliminado correctamente";
        } else {
            return "Usuario no encontrado";
        }
    }
}