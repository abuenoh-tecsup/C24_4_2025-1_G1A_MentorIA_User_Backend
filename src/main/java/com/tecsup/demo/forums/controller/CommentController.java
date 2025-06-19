package com.tecsup.demo.forums.controller;

import com.tecsup.demo.forums.dto.CommentDTO;
import com.tecsup.demo.forums.model.Comment;
import com.tecsup.demo.forums.model.Forum;
import com.tecsup.demo.forums.repository.CommentRepository;
import com.tecsup.demo.forums.repository.ForumRepository;
import com.tecsup.demo.authentication.model.User;
import com.tecsup.demo.authentication.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
    public ResponseEntity<?> crearComment(@RequestBody CommentDTO dto) {
        Optional<User> userOpt = userRepository.findById(dto.getUserId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Usuario no encontrado"));
        }

        Optional<Forum> forumOpt = forumRepository.findById(dto.getForumId());
        if (forumOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Foro no encontrado"));
        }

        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setCreationDate(dto.getCreationDate());
        comment.setUser(userOpt.get());
        comment.setForum(forumOpt.get());

        commentRepository.save(comment);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Comentario creado correctamente"));
    }

    @GetMapping
    public ResponseEntity<List<Comment>> listarComments() {
        return ResponseEntity.ok(commentRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerComment(@PathVariable Long id) {
        Optional<Comment> commentOpt = commentRepository.findById(id);
        if (commentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Comentario no encontrado"));
        }
        return ResponseEntity.ok(commentOpt.get());
    }

    @GetMapping("/forum/{forumId}")
    public ResponseEntity<List<Comment>> listarCommentsPorForo(@PathVariable Long forumId) {
        List<Comment> comments = commentRepository.findByForumIdOrderByCreationDate(forumId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Comment>> listarCommentsPorUsuario(@PathVariable Long userId) {
        List<Comment> comments = commentRepository.findByUserIdOrderByCreationDateDesc(userId);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarComment(@PathVariable Long id, @RequestBody CommentDTO dto) {
        Optional<Comment> commentOpt = commentRepository.findById(id);
        if (commentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Comentario no encontrado"));
        }

        Comment comment = commentOpt.get();

        if (!comment.getUser().getId().equals(dto.getUserId())) {
            Optional<User> userOpt = userRepository.findById(dto.getUserId());
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Usuario no encontrado"));
            }
            comment.setUser(userOpt.get());
        }

        if (!comment.getForum().getId().equals(dto.getForumId())) {
            Optional<Forum> forumOpt = forumRepository.findById(dto.getForumId());
            if (forumOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Foro no encontrado"));
            }
            comment.setForum(forumOpt.get());
        }

        comment.setContent(dto.getContent());
        comment.setCreationDate(dto.getCreationDate());

        commentRepository.save(comment);

        return ResponseEntity.ok(Map.of("message", "Comentario actualizado correctamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarComment(@PathVariable Long id) {
        if (!commentRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Comentario no encontrado"));
        }

        commentRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Comentario eliminado correctamente"));
    }
}
