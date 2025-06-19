package com.tecsup.demo.courses.controller;

import com.tecsup.demo.courses.dto.ModuleDTO;
import com.tecsup.demo.courses.model.Module;
import com.tecsup.demo.courses.model.Course;
import com.tecsup.demo.courses.repository.ModuleRepository;
import com.tecsup.demo.courses.repository.CourseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/modules")
public class ModuleController {

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private CourseRepository courseRepository;

    // ✅ Crear módulo
    @PostMapping
    public ResponseEntity<?> crearModule(@RequestBody ModuleDTO dto) {
        Optional<Course> courseOpt = courseRepository.findById(dto.getCourseId());
        if (courseOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Curso no encontrado"));
        }

        if (moduleRepository.existsByCourseIdAndModuleOrder(dto.getCourseId(), dto.getModuleOrder())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Ya existe un módulo con el orden " + dto.getModuleOrder() + " en este curso"));
        }

        Module module = new Module();
        module.setTitle(dto.getTitle());
        module.setDescription(dto.getDescription());
        module.setModuleOrder(dto.getModuleOrder());
        module.setCourse(courseOpt.get());

        moduleRepository.save(module);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Módulo creado correctamente"));
    }

    // ✅ Listar todos los módulos
    @GetMapping
    public ResponseEntity<List<Module>> listarModules() {
        return ResponseEntity.ok(moduleRepository.findAll());
    }

    // ✅ Obtener módulo por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerModule(@PathVariable Long id) {
        Optional<Module> moduleOpt = moduleRepository.findById(id);
        if (moduleOpt.isPresent()) {
            return ResponseEntity.ok(moduleOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Módulo no encontrado"));
        }
    }

    // ✅ Listar módulos por curso
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Module>> listarModulesPorCurso(@PathVariable Long courseId) {
        return ResponseEntity.ok(moduleRepository.findByCourseIdOrderByModuleOrder(courseId));
    }

    // ✅ Actualizar módulo
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarModule(@PathVariable Long id, @RequestBody ModuleDTO dto) {
        Optional<Module> moduleOpt = moduleRepository.findById(id);
        if (moduleOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Módulo no encontrado"));
        }

        Module module = moduleOpt.get();

        if (!module.getCourse().getId().equals(dto.getCourseId())) {
            Optional<Course> courseOpt = courseRepository.findById(dto.getCourseId());
            if (courseOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Curso no encontrado"));
            }
            module.setCourse(courseOpt.get());
        }

        // Si cambia el orden, verificar conflictos
        boolean ordenCambiado = !module.getModuleOrder().equals(dto.getModuleOrder());
        boolean ordenExiste = moduleRepository.existsByCourseIdAndModuleOrder(dto.getCourseId(), dto.getModuleOrder());

        if (ordenCambiado && ordenExiste) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Ya existe un módulo con el orden " + dto.getModuleOrder() + " en este curso"));
        }

        module.setTitle(dto.getTitle());
        module.setDescription(dto.getDescription());
        module.setModuleOrder(dto.getModuleOrder());

        moduleRepository.save(module);

        return ResponseEntity.ok(Map.of("message", "Módulo actualizado correctamente"));
    }

    // ✅ Eliminar módulo
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarModule(@PathVariable Long id) {
        if (moduleRepository.existsById(id)) {
            moduleRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Módulo eliminado correctamente"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Módulo no encontrado"));
        }
    }
}
