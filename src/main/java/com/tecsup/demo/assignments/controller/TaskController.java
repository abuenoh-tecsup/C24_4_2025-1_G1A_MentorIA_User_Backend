package com.tecsup.demo.assignments.controller;

import com.tecsup.demo.assignments.dto.TaskDTO;
import com.tecsup.demo.assignments.model.Task;
import com.tecsup.demo.courses.model.Module;
import com.tecsup.demo.assignments.repository.TaskRepository;
import com.tecsup.demo.courses.repository.ModuleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @PostMapping
    public ResponseEntity<?> crearTask(@RequestBody TaskDTO dto) {
        Optional<Module> moduleOpt = moduleRepository.findById(dto.getModuleId());
        if (moduleOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Módulo no encontrado"));
        }

        if (dto.getDueDate().isBefore(dto.getPublicationDate())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "La fecha de vencimiento no puede ser anterior a la fecha de publicación"));
        }

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPublicationDate(dto.getPublicationDate());
        task.setDueDate(dto.getDueDate());
        task.setModule(moduleOpt.get());

        taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Tarea creada correctamente"));
    }

    @GetMapping
    public ResponseEntity<List<Task>> listarTasks() {
        return ResponseEntity.ok(taskRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerTask(@PathVariable Long id) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Tarea no encontrada"));
        }
        return ResponseEntity.ok(taskOpt.get());
    }

    @GetMapping("/module/{moduleId}")
    public ResponseEntity<?> listarTasksPorModulo(@PathVariable Long moduleId) {
        if (!moduleRepository.existsById(moduleId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Módulo no encontrado"));
        }
        List<Task> tasks = taskRepository.findByModuleIdOrderByDueDateAsc(moduleId);
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarTask(@PathVariable Long id, @RequestBody TaskDTO dto) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Tarea no encontrada"));
        }

        Task task = taskOpt.get();

        if (!task.getModule().getId().equals(dto.getModuleId())) {
            Optional<Module> moduleOpt = moduleRepository.findById(dto.getModuleId());
            if (moduleOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Módulo no encontrado"));
            }
            task.setModule(moduleOpt.get());
        }

        if (dto.getDueDate().isBefore(dto.getPublicationDate())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "La fecha de vencimiento no puede ser anterior a la fecha de publicación"));
        }

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPublicationDate(dto.getPublicationDate());
        task.setDueDate(dto.getDueDate());

        taskRepository.save(task);
        return ResponseEntity.ok(Map.of("message", "Tarea actualizada correctamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarTask(@PathVariable Long id) {
        if (!taskRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Tarea no encontrada"));
        }

        taskRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Tarea eliminada correctamente"));
    }
}
