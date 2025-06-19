package com.tecsup.demo.content.controller;

import com.tecsup.demo.content.dto.MaterialDTO;
import com.tecsup.demo.content.model.Material;
import com.tecsup.demo.courses.model.Module;
import com.tecsup.demo.content.repository.MaterialRepository;
import com.tecsup.demo.courses.repository.ModuleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/materials")
public class MaterialController {

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @PostMapping
    public ResponseEntity<?> crearMaterial(@RequestBody MaterialDTO dto) {
        Optional<Module> moduleOpt = moduleRepository.findById(dto.getModuleId());
        if (moduleOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Módulo no encontrado"));
        }

        Material material = new Material();
        material.setTitle(dto.getTitle());
        material.setDescription(dto.getDescription());
        material.setType(dto.getType());
        material.setResourceUrl(dto.getResourceUrl());
        material.setCreationDate(LocalDateTime.now());
        material.setModule(moduleOpt.get());

        materialRepository.save(material);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Material creado correctamente"));
    }

    @GetMapping
    public ResponseEntity<List<Material>> listarMateriales() {
        return ResponseEntity.ok(materialRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerMaterial(@PathVariable Long id) {
        Optional<Material> materialOpt = materialRepository.findById(id);
        if (materialOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Material no encontrado"));
        }
        return ResponseEntity.ok(materialOpt.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarMaterial(@PathVariable Long id, @RequestBody MaterialDTO dto) {
        Optional<Material> materialOpt = materialRepository.findById(id);
        if (materialOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Material no encontrado"));
        }

        Material material = materialOpt.get();
        material.setTitle(dto.getTitle());
        material.setDescription(dto.getDescription());
        material.setType(dto.getType());
        material.setResourceUrl(dto.getResourceUrl());

        if (!material.getModule().getId().equals(dto.getModuleId())) {
            Optional<Module> moduleOpt = moduleRepository.findById(dto.getModuleId());
            if (moduleOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Nuevo módulo no encontrado"));
            }
            material.setModule(moduleOpt.get());
        }

        materialRepository.save(material);
        return ResponseEntity.ok(Map.of("message", "Material actualizado correctamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarMaterial(@PathVariable Long id) {
        if (!materialRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Material no encontrado"));
        }

        materialRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Material eliminado correctamente"));
    }
}
