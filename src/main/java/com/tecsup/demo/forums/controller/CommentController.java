package com.tecsup.demo.forums.controller;

import com.tecsup.demo.forums.dto.CommentDTO;
import com.tecsup.demo.forums.model.Comment;
import com.tecsup.demo.forums.model.Forum;
import com.tecsup.demo.forums.repository.CommentRepository;
import com.tecsup.demo.forums.repository.ForumRepository;
import com.tecsup.demo.authentication.model.User;
import com.tecsup.demo.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ForumRepository forumRepository;

    @PostMapping
    public String crearComment(@RequestBody CommentDTO dto) {
        // Verificar si el usuario existe
        Optional<User> userOpt = userRepository.findById(dto.getUserId());
        if (userOpt.isEmpty()) {
            return "Usuario no encontrado";
        }

        // Verificar si el foro existe
        Optional<Forum> forumOpt = forumRepository.findById(dto.getForumId());
        if (forumOpt.isEmpty()) {
            return "Foro no encontrado";
        }

        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setCreationDate(dto.getCreationDate());
        comment.setUser(userOpt.get());
        comment.setForum(forumOpt.get());

        commentRepository.save(comment);
        return "Comentario creado correctamente";
    }

    @GetMapping
    public List<Comment> listarComments() {
        return commentRepository.findAll();
    }

    @GetMapping("/{id}")
    public Comment obtenerComment(@PathVariable Long id) {
        return commentRepository.findById(id).orElse(null);
    }

    @GetMapping("/forum/{forumId}")
    public List<Comment> listarCommentsPorForo(@PathVariable Long forumId) {
        return commentRepository.findByForumIdOrderByCreationDate(forumId);
    }

    @GetMapping("/user/{userId}")
    public List<Comment> listarCommentsPorUsuario(@PathVariable Long userId) {
        return commentRepository.findByUserIdOrderByCreationDateDesc(userId);
    }

    @PutMapping("/{id}")
    public String actualizarComment(@PathVariable Long id, @RequestBody CommentDTO dto) {
        Optional<Comment> commentOpt = commentRepository.findById(id);
        if (commentOpt.isEmpty()) {
            return "Comentario no encontrado";
        }

        Comment comment = commentOpt.get();

        // Si se cambia el usuario, verificar que exista
        if (!comment.getUser().getId().equals(dto.getUserId())) {
            Optional<User> userOpt = userRepository.findById(dto.getUserId());
            if (userOpt.isEmpty()) {
                return "Usuario no encontrado";
            }
            comment.setUser(userOpt.get());
        }

        // Si se cambia el foro, verificar que exista
        if (!comment.getForum().getId().equals(dto.getForumId())) {
            Optional<Forum> forumOpt = forumRepository.findById(dto.getForumId());
            if (forumOpt.isEmpty()) {
                return "Foro no encontrado";
            }
            comment.setForum(forumOpt.get());
        }

        comment.setContent(dto.getContent());
        comment.setCreationDate(dto.getCreationDate());

        commentRepository.save(comment);
        return "Comentario actualizado correctamente";
    }

    @DeleteMapping("/{id}")
    public String eliminarComment(@PathVariable Long id) {
        if (commentRepository.existsById(id)) {
            commentRepository.deleteById(id);
            return "Comentario eliminado correctamente";
        } else {
            return "Comentario no encontrado";
        }
    }
}