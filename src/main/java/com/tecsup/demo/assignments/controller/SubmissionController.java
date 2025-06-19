package com.tecsup.demo.assignments.controller;

import com.tecsup.demo.assignments.dto.SubmissionDTO;
import com.tecsup.demo.assignments.model.Submission;
import com.tecsup.demo.assignments.model.Task;
import com.tecsup.demo.authentication.model.User;
import com.tecsup.demo.assignments.repository.SubmissionRepository;
import com.tecsup.demo.assignments.repository.TaskRepository;
import com.tecsup.demo.authentication.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    @Autowired
    private SubmissionRepository submissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @PostMapping
    public ResponseEntity<?> crearSubmission(@RequestBody SubmissionDTO dto) {
        Optional<User> userOpt = userRepository.findById(dto.getUserId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Usuario no encontrado"));
        }

        Optional<Task> taskOpt = taskRepository.findById(dto.getTaskId());
        if (taskOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Tarea no encontrada"));
        }

        if (submissionRepository.existsByUserIdAndTaskId(dto.getUserId(), dto.getTaskId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Ya existe una entrega de este usuario para esta tarea"));
        }

        Submission submission = new Submission();
        submission.setSubmissionDate(LocalDateTime.now());
        submission.setUser(userOpt.get());
        submission.setTask(taskOpt.get());
        submission.setStatus(dto.getStatus());
        submission.setGrade(dto.getGrade());
        submission.setComments(dto.getComments());
        submission.setFileUrl(dto.getFileUrl());

        submissionRepository.save(submission);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Entrega creada correctamente"));
    }

    @GetMapping
    public ResponseEntity<List<Submission>> listarSubmissions() {
        return ResponseEntity.ok(submissionRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerSubmission(@PathVariable Long id) {
        return submissionRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Entrega no encontrada")));
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<?> listarSubmissionsPorTarea(@PathVariable Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Tarea no encontrada"));
        }
        List<Submission> submissions = submissionRepository.findByTaskIdOrderBySubmissionDateDesc(taskId);
        return ResponseEntity.ok(submissions);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> listarSubmissionsPorUsuario(@PathVariable Long userId) {
        if (!userRepository.existsById(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Usuario no encontrado"));
        }
        List<Submission> submissions = submissionRepository.findByUserIdOrderBySubmissionDateDesc(userId);
        return ResponseEntity.ok(submissions);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarSubmission(@PathVariable Long id, @RequestBody SubmissionDTO dto) {
        Optional<Submission> submissionOpt = submissionRepository.findById(id);
        if (submissionOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Entrega no encontrada"));
        }

        Submission submission = submissionOpt.get();

        boolean userChanged = !submission.getUser().getId().equals(dto.getUserId());
        boolean taskChanged = !submission.getTask().getId().equals(dto.getTaskId());

        if (userChanged) {
            Optional<User> userOpt = userRepository.findById(dto.getUserId());
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Usuario no encontrado"));
            }
            submission.setUser(userOpt.get());
        }

        if (taskChanged) {
            Optional<Task> taskOpt = taskRepository.findById(dto.getTaskId());
            if (taskOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Tarea no encontrada"));
            }
            submission.setTask(taskOpt.get());
        }

        if ((userChanged || taskChanged) &&
                submissionRepository.existsByUserIdAndTaskId(dto.getUserId(), dto.getTaskId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Ya existe una entrega de este usuario para esta tarea"));
        }

        submission.setStatus(dto.getStatus());
        submission.setGrade(dto.getGrade());
        submission.setComments(dto.getComments());
        submission.setFileUrl(dto.getFileUrl());

        submissionRepository.save(submission);
        return ResponseEntity.ok(Map.of("message", "Entrega actualizada correctamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarSubmission(@PathVariable Long id) {
        if (!submissionRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Entrega no encontrada"));
        }

        submissionRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Entrega eliminada correctamente"));
    }
}
