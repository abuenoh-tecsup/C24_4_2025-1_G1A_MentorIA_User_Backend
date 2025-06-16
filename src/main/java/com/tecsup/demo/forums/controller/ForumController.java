package com.tecsup.demo.forums.controller;

import com.tecsup.demo.forums.dto.ForumDTO;
import com.tecsup.demo.forums.model.Forum;
import com.tecsup.demo.forums.repository.ForumRepository;
import com.tecsup.demo.authentication.model.User;
import com.tecsup.demo.authentication.repository.UserRepository;
import com.tecsup.demo.courses.model.Module;
import com.tecsup.demo.courses.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
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
    public String crearForum(@RequestBody ForumDTO dto) {
        // Verificar si el autor existe
        Optional<User> authorOpt = userRepository.findById(dto.getAuthorId());
        if (authorOpt.isEmpty()) {
            return "Usuario autor no encontrado";
        }

        // Verificar si el módulo existe
        Optional<Module> moduleOpt = moduleRepository.findById(dto.getModuleId());
        if (moduleOpt.isEmpty()) {
            return "Módulo no encontrado";
        }

        // Verificar si ya existe un foro con el mismo título en el módulo
        if (forumRepository.existsByModuleIdAndTitle(dto.getModuleId(), dto.getTitle())) {
            return "Ya existe un foro con el título '" + dto.getTitle() + "' en este módulo";
        }

        Forum forum = new Forum();
        forum.setTitle(dto.getTitle());
        forum.setDescription(dto.getDescription());
        forum.setCreationDate(dto.getCreationDate());
        forum.setAuthor(authorOpt.get());
        forum.setModule(moduleOpt.get());

        forumRepository.save(forum);
        return "Foro creado correctamente";
    }

    @GetMapping
    public List<Forum> listarForums() {
        return forumRepository.findAll();
    }

    @GetMapping("/{id}")
    public Forum obtenerForum(@PathVariable Long id) {
        return forumRepository.findById(id).orElse(null);
    }

    @GetMapping("/module/{moduleId}")
    public List<Forum> listarForumsPorModulo(@PathVariable Long moduleId) {
        return forumRepository.findByModuleIdOrderByCreationDateDesc(moduleId);
    }

    @GetMapping("/author/{authorId}")
    public List<Forum> listarForumsPorAutor(@PathVariable Long authorId) {
        return forumRepository.findByAuthorIdOrderByCreationDateDesc(authorId);
    }

    @PutMapping("/{id}")
    public String actualizarForum(@PathVariable Long id, @RequestBody ForumDTO dto) {
        Optional<Forum> forumOpt = forumRepository.findById(id);
        if (forumOpt.isEmpty()) {
            return "Foro no encontrado";
        }

        Forum forum = forumOpt.get();

        // Si se cambia el autor, verificar que exista
        if (!forum.getAuthor().getId().equals(dto.getAuthorId())) {
            Optional<User> authorOpt = userRepository.findById(dto.getAuthorId());
            if (authorOpt.isEmpty()) {
                return "Usuario autor no encontrado";
            }
            forum.setAuthor(authorOpt.get());
        }

        // Si se cambia el módulo, verificar que exista
        if (!forum.getModule().getId().equals(dto.getModuleId())) {
            Optional<Module> moduleOpt = moduleRepository.findById(dto.getModuleId());
            if (moduleOpt.isEmpty()) {
                return "Módulo no encontrado";
            }
            forum.setModule(moduleOpt.get());
        }

        // Verificar si el nuevo título entra en conflicto con otro foro del módulo
        if (!forum.getTitle().equals(dto.getTitle()) &&
                forumRepository.existsByModuleIdAndTitle(dto.getModuleId(), dto.getTitle())) {
            return "Ya existe un foro con el título '" + dto.getTitle() + "' en este módulo";
        }

        forum.setTitle(dto.getTitle());
        forum.setDescription(dto.getDescription());
        forum.setCreationDate(dto.getCreationDate());

        forumRepository.save(forum);
        return "Foro actualizado correctamente";
    }

    @DeleteMapping("/{id}")
    public String eliminarForum(@PathVariable Long id) {
        if (forumRepository.existsById(id)) {
            forumRepository.deleteById(id);
            return "Foro eliminado correctamente";
        } else {
            return "Foro no encontrado";
        }
    }
}