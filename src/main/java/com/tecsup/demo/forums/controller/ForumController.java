package com.tecsup.demo.forums.controller;

import com.tecsup.demo.forums.dto.ForumDTO;
import com.tecsup.demo.forums.model.Forum;
import com.tecsup.demo.forums.repository.ForumRepository;
import com.tecsup.demo.authentication.model.User;
import com.tecsup.demo.authentication.repository.UserRepository;
import com.tecsup.demo.courses.model.Module;
import com.tecsup.demo.courses.repository.ModuleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/forums")
public class ForumController {

    @Autowired
    private ForumRepository forumRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @PostMapping
    public ResponseEntity<?> crearForum(@RequestBody ForumDTO dto) {
        Optional<User> authorOpt = userRepository.findById(dto.getAuthorId());
        if (authorOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Usuario autor no encontrado"));
        }

        Optional<Module> moduleOpt = moduleRepository.findById(dto.getModuleId());
        if (moduleOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Módulo no encontrado"));
        }

        if (forumRepository.existsByModuleIdAndTitle(dto.getModuleId(), dto.getTitle())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Ya existe un foro con el título '" + dto.getTitle() + "' en este módulo"));
        }

        Forum forum = new Forum();
        forum.setTitle(dto.getTitle());
        forum.setDescription(dto.getDescription());
        forum.setCreationDate(dto.getCreationDate());
        forum.setAuthor(authorOpt.get());
        forum.setModule(moduleOpt.get());

        forumRepository.save(forum);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Foro creado correctamente"));
    }

    @GetMapping
    public ResponseEntity<List<Forum>> listarForums() {
        return ResponseEntity.ok(forumRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerForum(@PathVariable Long id) {
        Optional<Forum> forumOpt = forumRepository.findById(id);
        if (forumOpt.isPresent()) {
            return ResponseEntity.ok(forumOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Foro no encontrado"));
        }
    }

    @GetMapping("/module/{moduleId}")
    public ResponseEntity<List<Forum>> listarForumsPorModulo(@PathVariable Long moduleId) {
        return ResponseEntity.ok(forumRepository.findByModuleIdOrderByCreationDateDesc(moduleId));
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<Forum>> listarForumsPorAutor(@PathVariable Long authorId) {
        return ResponseEntity.ok(forumRepository.findByAuthorIdOrderByCreationDateDesc(authorId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarForum(@PathVariable Long id, @RequestBody ForumDTO dto) {
        Optional<Forum> forumOpt = forumRepository.findById(id);
        if (forumOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Foro no encontrado"));
        }

        Forum forum = forumOpt.get();

        if (!forum.getAuthor().getId().equals(dto.getAuthorId())) {
            Optional<User> authorOpt = userRepository.findById(dto.getAuthorId());
            if (authorOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Usuario autor no encontrado"));
            }
            forum.setAuthor(authorOpt.get());
        }

        if (!forum.getModule().getId().equals(dto.getModuleId())) {
            Optional<Module> moduleOpt = moduleRepository.findById(dto.getModuleId());
            if (moduleOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Módulo no encontrado"));
            }
            forum.setModule(moduleOpt.get());
        }

        if (!forum.getTitle().equals(dto.getTitle()) &&
                forumRepository.existsByModuleIdAndTitle(dto.getModuleId(), dto.getTitle())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Ya existe un foro con el título '" + dto.getTitle() + "' en este módulo"));
        }

        forum.setTitle(dto.getTitle());
        forum.setDescription(dto.getDescription());
        forum.setCreationDate(dto.getCreationDate());

        forumRepository.save(forum);
        return ResponseEntity.ok(Map.of("message", "Foro actualizado correctamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarForum(@PathVariable Long id) {
        if (forumRepository.existsById(id)) {
            forumRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Foro eliminado correctamente"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Foro no encontrado"));
        }
    }
}
