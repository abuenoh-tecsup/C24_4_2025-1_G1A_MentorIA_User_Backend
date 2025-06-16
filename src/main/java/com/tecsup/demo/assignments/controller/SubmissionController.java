package com.tecsup.demo.assignments.controller;

import com.tecsup.demo.assignments.dto.SubmissionDTO;
import com.tecsup.demo.assignments.model.Submission;
import com.tecsup.demo.assignments.model.Task;
import com.tecsup.demo.authentication.model.User;
import com.tecsup.demo.assignments.repository.SubmissionRepository;
import com.tecsup.demo.assignments.repository.TaskRepository;
import com.tecsup.demo.authentication.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    public String crearSubmission(@RequestBody SubmissionDTO dto) {
        // Verificar si el usuario existe
        Optional<User> userOpt = userRepository.findById(dto.getUserId());
        if (userOpt.isEmpty()) {
            return "Usuario no encontrado";
        }

        // Verificar si la tarea existe
        Optional<Task> taskOpt = taskRepository.findById(dto.getTaskId());
        if (taskOpt.isEmpty()) {
            return "Tarea no encontrada";
        }

        // Verificar si ya existe una entrega para este usuario y tarea
        if (submissionRepository.existsByUserIdAndTaskId(dto.getUserId(), dto.getTaskId())) {
            return "Ya existe una entrega de este usuario para esta tarea";
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
        return "Entrega creada correctamente";
    }

    @GetMapping
    public List<Submission> listarSubmissions() {
        return submissionRepository.findAll();
    }

    @GetMapping("/{id}")
    public Submission obtenerSubmission(@PathVariable Long id) {
        return submissionRepository.findById(id).orElse(null);
    }

    @GetMapping("/task/{taskId}")
    public List<Submission> listarSubmissionsPorTarea(@PathVariable Long taskId) {
        return submissionRepository.findByTaskIdOrderBySubmissionDateDesc(taskId);
    }

    @GetMapping("/user/{userId}")
    public List<Submission> listarSubmissionsPorUsuario(@PathVariable Long userId) {
        return submissionRepository.findByUserIdOrderBySubmissionDateDesc(userId);
    }

    @PutMapping("/{id}")
    public String actualizarSubmission(@PathVariable Long id, @RequestBody SubmissionDTO dto) {
        Optional<Submission> submissionOpt = submissionRepository.findById(id);
        if (submissionOpt.isEmpty()) {
            return "Entrega no encontrada";
        }

        Submission submission = submissionOpt.get();

        // Si se cambia el usuario, verificar que exista
        if (!submission.getUser().getId().equals(dto.getUserId())) {
            Optional<User> userOpt = userRepository.findById(dto.getUserId());
            if (userOpt.isEmpty()) {
                return "Usuario no encontrado";
            }
            submission.setUser(userOpt.get());
        }

        // Si se cambia la tarea, verificar que exista
        if (!submission.getTask().getId().equals(dto.getTaskId())) {
            Optional<Task> taskOpt = taskRepository.findById(dto.getTaskId());
            if (taskOpt.isEmpty()) {
                return "Tarea no encontrada";
            }
            submission.setTask(taskOpt.get());
        }

        // Verificar duplicados si cambió usuario o tarea
        if ((!submission.getUser().getId().equals(dto.getUserId()) ||
                !submission.getTask().getId().equals(dto.getTaskId())) &&
                submissionRepository.existsByUserIdAndTaskId(dto.getUserId(), dto.getTaskId())) {
            return "Ya existe una entrega de este usuario para esta tarea";
        }

        submission.setStatus(dto.getStatus());
        submission.setGrade(dto.getGrade());
        submission.setComments(dto.getComments());
        submission.setFileUrl(dto.getFileUrl());

        submissionRepository.save(submission);
        return "Entrega actualizada correctamente";
    }

    @DeleteMapping("/{id}")
    public String eliminarSubmission(@PathVariable Long id) {
        if (submissionRepository.existsById(id)) {
            submissionRepository.deleteById(id);
            return "Entrega eliminada correctamente";
        } else {
            return "Entrega no encontrada";
        }
    }
}