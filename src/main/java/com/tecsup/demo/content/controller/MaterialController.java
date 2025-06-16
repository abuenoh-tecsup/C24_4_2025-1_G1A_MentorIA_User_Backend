package com.tecsup.demo.content.controller;

import com.tecsup.demo.content.dto.MaterialDTO;
import com.tecsup.demo.content.model.Material;
import com.tecsup.demo.courses.model.Module;
import com.tecsup.demo.content.repository.MaterialRepository;
import com.tecsup.demo.courses.repository.ModuleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/materials")
public class MaterialController {

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @PostMapping
    public String crearMaterial(@RequestBody MaterialDTO dto) {
        Optional<Module> moduleOpt = moduleRepository.findById(dto.getModuleId());
        if (moduleOpt.isEmpty()) {
            return "Módulo no encontrado";
        }

        Material material = new Material();
        material.setTitle(dto.getTitle());
        material.setDescription(dto.getDescription());
        material.setType(dto.getType());
        material.setResourceUrl(dto.getResourceUrl());
        material.setCreationDate(LocalDateTime.now());
        material.setModule(moduleOpt.get());

        materialRepository.save(material);
        return "Material creado correctamente";
    }

    @GetMapping
    public List<Material> listarMateriales() {
        return materialRepository.findAll();
    }

    @GetMapping("/{id}")
    public Material obtenerMaterial(@PathVariable Long id) {
        return materialRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public String actualizarMaterial(@PathVariable Long id, @RequestBody MaterialDTO dto) {
        Optional<Material> materialOpt = materialRepository.findById(id);
        if (materialOpt.isEmpty()) {
            return "Material no encontrado";
        }

        Material material = materialOpt.get();
        material.setTitle(dto.getTitle());
        material.setDescription(dto.getDescription());
        material.setType(dto.getType());
        material.setResourceUrl(dto.getResourceUrl());

        if (!material.getModule().getId().equals(dto.getModuleId())) {
            Optional<Module> moduleOpt = moduleRepository.findById(dto.getModuleId());
            moduleOpt.ifPresent(material::setModule);
        }

        materialRepository.save(material);
        return "Material actualizado correctamente";
    }

    @DeleteMapping("/{id}")
    public String eliminarMaterial(@PathVariable Long id) {
        if (materialRepository.existsById(id)) {
            materialRepository.deleteById(id);
            return "Material eliminado correctamente";
        } else {
            return "Material no encontrado";
        }
    }
}
