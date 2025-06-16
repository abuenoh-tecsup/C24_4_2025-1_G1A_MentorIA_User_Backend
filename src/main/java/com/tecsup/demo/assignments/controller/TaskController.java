package com.tecsup.demo.assignments.controller;

import com.tecsup.demo.assignments.dto.TaskDTO;
import com.tecsup.demo.assignments.model.Task;
import com.tecsup.demo.courses.model.Module;
import com.tecsup.demo.assignments.repository.TaskRepository;
import com.tecsup.demo.courses.repository.ModuleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @PostMapping
    public String crearTask(@RequestBody TaskDTO dto) {
        // Verificar si el módulo existe
        Optional<Module> moduleOpt = moduleRepository.findById(dto.getModuleId());
        if (moduleOpt.isEmpty()) {
            return "Módulo no encontrado";
        }

        // Validar fechas
        if (dto.getDueDate().isBefore(dto.getPublicationDate())) {
            return "La fecha de vencimiento no puede ser anterior a la fecha de publicación";
        }

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPublicationDate(dto.getPublicationDate());
        task.setDueDate(dto.getDueDate());
        task.setModule(moduleOpt.get());

        taskRepository.save(task);
        return "Tarea creada correctamente";
    }

    @GetMapping
    public List<Task> listarTasks() {
        return taskRepository.findAll();
    }

    @GetMapping("/{id}")
    public Task obtenerTask(@PathVariable Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    @GetMapping("/module/{moduleId}")
    public List<Task> listarTasksPorModulo(@PathVariable Long moduleId) {
        return taskRepository.findByModuleIdOrderByDueDateAsc(moduleId);
    }

    @PutMapping("/{id}")
    public String actualizarTask(@PathVariable Long id, @RequestBody TaskDTO dto) {
        Optional<Task> taskOpt = taskRepository.findById(id);
        if (taskOpt.isEmpty()) {
            return "Tarea no encontrada";
        }

        Task task = taskOpt.get();

        // Si se cambia el módulo, verificar que exista
        if (!task.getModule().getId().equals(dto.getModuleId())) {
            Optional<Module> moduleOpt = moduleRepository.findById(dto.getModuleId());
            if (moduleOpt.isEmpty()) {
                return "Módulo no encontrado";
            }
            task.setModule(moduleOpt.get());
        }

        // Validar fechas
        if (dto.getDueDate().isBefore(dto.getPublicationDate())) {
            return "La fecha de vencimiento no puede ser anterior a la fecha de publicación";
        }

        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setPublicationDate(dto.getPublicationDate());
        task.setDueDate(dto.getDueDate());

        taskRepository.save(task);
        return "Tarea actualizada correctamente";
    }

    @DeleteMapping("/{id}")
    public String eliminarTask(@PathVariable Long id) {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
            return "Tarea eliminada correctamente";
        } else {
            return "Tarea no encontrada";
        }
    }
}