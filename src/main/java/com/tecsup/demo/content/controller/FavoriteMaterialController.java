package com.tecsup.demo.content.controller;

import com.tecsup.demo.content.dto.FavoriteMaterialDTO;
import com.tecsup.demo.content.model.FavoriteMaterial;
import com.tecsup.demo.content.model.Material;
import com.tecsup.demo.authentication.model.User;
import com.tecsup.demo.content.repository.FavoriteMaterialRepository;
import com.tecsup.demo.content.repository.MaterialRepository;
import com.tecsup.demo.authentication.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

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
    public ResponseEntity<?> agregarFavorito(@RequestBody FavoriteMaterialDTO dto) {
        Optional<User> userOpt = userRepository.findById(dto.getUserId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Usuario no encontrado"));
        }

        Optional<Material> materialOpt = materialRepository.findById(dto.getMaterialId());
        if (materialOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Material no encontrado"));
        }

        if (favoriteMaterialRepository.existsByUserIdAndMaterialId(dto.getUserId(), dto.getMaterialId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "El material ya está en favoritos de este usuario"));
        }

        FavoriteMaterial favorite = new FavoriteMaterial();
        favorite.setDate(LocalDateTime.now());
        favorite.setUser(userOpt.get());
        favorite.setMaterial(materialOpt.get());

        favoriteMaterialRepository.save(favorite);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Material agregado a favoritos correctamente"));
    }

    @GetMapping
    public ResponseEntity<List<FavoriteMaterial>> listarTodosFavoritos() {
        return ResponseEntity.ok(favoriteMaterialRepository.findAll());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> listarFavoritosPorUsuario(@PathVariable Long userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Usuario no encontrado"));
        }
        List<FavoriteMaterial> favoritos = favoriteMaterialRepository.findByUserIdOrderByDateDesc(userId);
        return ResponseEntity.ok(favoritos);
    }

    @GetMapping("/material/{materialId}")
    public ResponseEntity<?> listarUsuariosPorMaterial(@PathVariable Long materialId) {
        if (!materialRepository.existsById(materialId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Material no encontrado"));
        }
        List<FavoriteMaterial> usuarios = favoriteMaterialRepository.findByMaterialIdOrderByDateDesc(materialId);
        return ResponseEntity.ok(usuarios);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarFavorito(@PathVariable Long id) {
        if (!favoriteMaterialRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Favorito no encontrado"));
        }

        favoriteMaterialRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Favorito eliminado correctamente"));
    }

    @DeleteMapping("/user/{userId}/material/{materialId}")
    public ResponseEntity<?> eliminarFavoritoPorUsuarioYMaterial(
            @PathVariable Long userId,
            @PathVariable Long materialId) {

        Optional<FavoriteMaterial> favoriteOpt = favoriteMaterialRepository.findByUserIdAndMaterialId(userId, materialId);

        if (favoriteOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Favorito no encontrado"));
        }

        favoriteMaterialRepository.delete(favoriteOpt.get());
        return ResponseEntity.ok(Map.of("message", "Favorito eliminado correctamente"));
    }

    @GetMapping("/check/{userId}/{materialId}")
    public ResponseEntity<?> verificarSiEsFavorito(@PathVariable Long userId, @PathVariable Long materialId) {
        boolean existe = favoriteMaterialRepository.existsByUserIdAndMaterialId(userId, materialId);
        return ResponseEntity.ok(Map.of("isFavorite", existe));
    }
}
