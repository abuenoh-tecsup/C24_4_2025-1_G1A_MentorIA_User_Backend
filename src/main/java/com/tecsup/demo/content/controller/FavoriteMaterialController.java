package com.tecsup.demo.content.controller;

import com.tecsup.demo.content.dto.FavoriteMaterialDTO;
import com.tecsup.demo.content.model.FavoriteMaterial;
import com.tecsup.demo.content.model.Material;
import com.tecsup.demo.authentication.model.User;
import com.tecsup.demo.content.repository.FavoriteMaterialRepository;
import com.tecsup.demo.content.repository.MaterialRepository;
import com.tecsup.demo.authentication.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteMaterialController {

    @Autowired
    private FavoriteMaterialRepository favoriteMaterialRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @PostMapping
    public String agregarFavorito(@RequestBody FavoriteMaterialDTO dto) {
        // Verificar si el usuario existe
        Optional<User> userOpt = userRepository.findById(dto.getUserId());
        if (userOpt.isEmpty()) {
            return "Usuario no encontrado";
        }

        // Verificar si el material existe
        Optional<Material> materialOpt = materialRepository.findById(dto.getMaterialId());
        if (materialOpt.isEmpty()) {
            return "Material no encontrado";
        }

        // Verificar si ya existe este favorito (evitar duplicados)
        if (favoriteMaterialRepository.existsByUserIdAndMaterialId(dto.getUserId(), dto.getMaterialId())) {
            return "El material ya está en favoritos de este usuario";
        }

        FavoriteMaterial favoriteMaterial = new FavoriteMaterial();
        favoriteMaterial.setDate(LocalDateTime.now());
        favoriteMaterial.setUser(userOpt.get());
        favoriteMaterial.setMaterial(materialOpt.get());

        favoriteMaterialRepository.save(favoriteMaterial);
        return "Material agregado a favoritos correctamente";
    }

    @GetMapping
    public List<FavoriteMaterial> listarTodosFavoritos() {
        return favoriteMaterialRepository.findAll();
    }

    @GetMapping("/user/{userId}")
    public List<FavoriteMaterial> listarFavoritosPorUsuario(@PathVariable Long userId) {
        return favoriteMaterialRepository.findByUserIdOrderByDateDesc(userId);
    }

    @GetMapping("/material/{materialId}")
    public List<FavoriteMaterial> listarUsuariosPorMaterial(@PathVariable Long materialId) {
        return favoriteMaterialRepository.findByMaterialIdOrderByDateDesc(materialId);
    }

    @DeleteMapping("/{id}")
    public String eliminarFavorito(@PathVariable Long id) {
        if (favoriteMaterialRepository.existsById(id)) {
            favoriteMaterialRepository.deleteById(id);
            return "Favorito eliminado correctamente";
        } else {
            return "Favorito no encontrado";
        }
    }

    @DeleteMapping("/user/{userId}/material/{materialId}")
    public String eliminarFavoritoPorUsuarioYMaterial(
            @PathVariable Long userId,
            @PathVariable Long materialId) {

        Optional<FavoriteMaterial> favoriteOpt =
                favoriteMaterialRepository.findByUserIdAndMaterialId(userId, materialId);

        if (favoriteOpt.isPresent()) {
            favoriteMaterialRepository.delete(favoriteOpt.get());
            return "Favorito eliminado correctamente";
        } else {
            return "Favorito no encontrado";
        }
    }

    @GetMapping("/check/{userId}/{materialId}")
    public boolean verificarSiEsFavorito(@PathVariable Long userId, @PathVariable Long materialId) {
        return favoriteMaterialRepository.existsByUserIdAndMaterialId(userId, materialId);
    }
}