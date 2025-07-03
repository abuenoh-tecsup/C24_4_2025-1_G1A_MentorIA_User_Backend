package com.tecsup.demo.content.controller;

import com.tecsup.demo.authentication.model.User;
import com.tecsup.demo.content.integration_ia.OpenAIService;
import com.tecsup.demo.content.integration_ia.PromptBuilderService;
import com.tecsup.demo.content.model.GeneratedContent;
import com.tecsup.demo.content.model.Material;
import com.tecsup.demo.authentication.repository.UserRepository;
import com.tecsup.demo.content.repository.GeneratedContentRepository;
import com.tecsup.demo.content.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/generated")
public class GeneratedContentController {

    @Autowired
    private OpenAIService openAIService;

    @Autowired
    private PromptBuilderService promptBuilderService;

    @Autowired
    private GeneratedContentRepository generatedContentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @PostMapping
    public ResponseEntity<?> crearContenidoGenerado(@RequestBody Map<String, Object> body) {
        System.out.println("BODY RECIBIDO: " + body);

        try {
            Long userId = Long.valueOf(body.get("userId").toString());
            Long materialId = Long.valueOf(body.get("materialId").toString());
            String contentType = body.get("contentType").toString();
            String outputFormat = body.get("outputFormat").toString();
            String content = body.get("content").toString();

            Optional<User> userOpt = userRepository.findById(userId);
            Optional<Material> materialOpt = materialRepository.findById(materialId);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Usuario no encontrado"));
            }

            if (materialOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Material no encontrado"));
            }

            GeneratedContent gen = new GeneratedContent();
            gen.setUser(userOpt.get());
            gen.setMaterial(materialOpt.get());
            gen.setContentType(GeneratedContent.ContentType.valueOf(contentType));
            gen.setOutputFormat(GeneratedContent.OutputFormat.valueOf(outputFormat));
            gen.setContent(content);
            gen.setCreationDate(LocalDateTime.now());

            generatedContentRepository.save(gen);

            return ResponseEntity.status(HttpStatus.CREATED).body(gen);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Error al crear contenido generado", "error", e.getMessage()));
        }
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generarContenidoConIA(@RequestBody Map<String, Object> body) {
        try {
            Long userId = Long.valueOf(body.get("userId").toString());
            Long materialId = Long.valueOf(body.get("materialId").toString());
            String contentType = body.get("contentType").toString();
            String outputFormat = body.get("outputFormat").toString();

            Optional<User> userOpt = userRepository.findById(userId);
            Optional<Material> materialOpt = materialRepository.findById(materialId);

            if (userOpt.isEmpty() || materialOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Usuario o material no encontrado"));
            }

            String textPlain = materialOpt.get().getTextPlain();
            if (textPlain == null || textPlain.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of("message", "El material no contiene texto"));
            }

            PromptBuilderService.PromptType promptType = switch (contentType.toLowerCase()) {
                case "summary" -> PromptBuilderService.PromptType.SUMMARY;
                case "flashcards" -> PromptBuilderService.PromptType.FLASHCARDS;
                default -> throw new IllegalArgumentException("Tipo no soportado");
            };

            String prompt = promptBuilderService.buildPrompt(promptType, textPlain);
            String contenidoGenerado = openAIService.generateText(prompt);

            GeneratedContent gen = new GeneratedContent();
            gen.setUser(userOpt.get());
            gen.setMaterial(materialOpt.get());
            gen.setContentType(GeneratedContent.ContentType.valueOf(contentType));
            gen.setOutputFormat(GeneratedContent.OutputFormat.valueOf(outputFormat));
            gen.setContent(contenidoGenerado);
            gen.setCreationDate(LocalDateTime.now());

            generatedContentRepository.save(gen);
            return ResponseEntity.status(HttpStatus.CREATED).body(gen);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Error al generar contenido", "error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<GeneratedContent>> listarTodos() {
        return ResponseEntity.ok(generatedContentRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        Optional<GeneratedContent> genOpt = generatedContentRepository.findById(id);

        if (genOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Contenido generado no encontrado"));
        }

        return ResponseEntity.ok(genOpt.get());
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<?> listarPorUsuario(@PathVariable Long userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Usuario no encontrado"));
        }

        List<GeneratedContent> lista = generatedContentRepository.findByUserId(userId);
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/material/{materialId}")
    public ResponseEntity<?> listarPorMaterial(@PathVariable Long materialId) {
        if (!materialRepository.existsById(materialId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Material no encontrado"));
        }

        List<GeneratedContent> lista = generatedContentRepository.findByMaterialId(materialId);
        return ResponseEntity.ok(lista);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarContenido(@PathVariable Long id) {
        if (!generatedContentRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Contenido no encontrado"));
        }

        generatedContentRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Contenido generado eliminado correctamente"));
    }
}
