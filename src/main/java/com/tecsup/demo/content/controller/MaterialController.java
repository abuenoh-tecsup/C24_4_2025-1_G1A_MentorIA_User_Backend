package com.tecsup.demo.content.controller;

import com.tecsup.demo.content.dto.MaterialDTO;
import com.tecsup.demo.content.model.Material;
import com.tecsup.demo.courses.model.Module;
import com.tecsup.demo.content.repository.MaterialRepository;
import com.tecsup.demo.courses.repository.ModuleRepository;
import org.springframework.web.multipart.MultipartFile;
import org.apache.tika.Tika;
import java.io.InputStream;

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
    private S3Service s3Service;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> crearMaterial(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("type") Material.MaterialType type,
            @RequestParam("moduleId") Long moduleId,
            @RequestParam("file") MultipartFile file
    ) {
        Optional<Module> moduleOpt = moduleRepository.findById(moduleId);
        if (moduleOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Módulo no encontrado"));
        }

        // Subir archivo a S3 (esto debes implementarlo con un servicio S3Service)
        String s3Url;
        try {
            s3Url = s3Service.uploadFile(file); // implementa este método
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al subir archivo a S3"));
        }

        // Extraer texto plano con Tika
        String extractedText = "";
        try (InputStream is = file.getInputStream()) {
            Tika tika = new Tika();
            extractedText = tika.parseToString(is);
        } catch (Exception e) {
            extractedText = "";
            System.err.println("Error al extraer texto con Tika: " + e.getMessage());
        }


        Material material = new Material();
        material.setTitle(title);
        material.setDescription(description);
        material.setType(type);
        material.setModule(moduleOpt.get());
        material.setResourceUrl(s3Url);
        material.setTextPlain(extractedText);
        material.setCreationDate(LocalDateTime.now());

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
        Optional<Material> materialOpt = materialRepository.findById(id);
        if (materialOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Material no encontrado"));
        }

        Material material = materialOpt.get();

        try {
            s3Service.deleteFile(material.getResourceUrl()); // Método que debes implementar
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error al eliminar archivo de S3"));
        }

        materialRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Material eliminado correctamente"));
    }

}
