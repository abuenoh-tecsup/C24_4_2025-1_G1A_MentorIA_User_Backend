package com.tecsup.demo.courses.controller;

import com.tecsup.demo.courses.dto.ModuleDTO;
import com.tecsup.demo.courses.model.Module;
import com.tecsup.demo.courses.model.Course;
import com.tecsup.demo.courses.repository.ModuleRepository;
import com.tecsup.demo.courses.repository.CourseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/modules")
public class ModuleController {

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private CourseRepository courseRepository;

    @PostMapping
    public String crearModule(@RequestBody ModuleDTO dto) {
        // Verificar si el curso existe
        Optional<Course> courseOpt = courseRepository.findById(dto.getCourseId());
        if (courseOpt.isEmpty()) {
            return "Curso no encontrado";
        }

        // Verificar si ya existe un módulo con el mismo orden en el curso
        if (moduleRepository.existsByCourseIdAndOrder(dto.getCourseId(), dto.getOrder())) {
            return "Ya existe un módulo con el orden " + dto.getOrder() + " en este curso";
        }

        Module module = new Module();
        module.setTitle(dto.getTitle());
        module.setDescription(dto.getDescription());
        module.setOrder(dto.getOrder());
        module.setCourse(courseOpt.get());

        moduleRepository.save(module);
        return "Módulo creado correctamente";
    }

    @GetMapping
    public List<Module> listarModules() {
        return moduleRepository.findAll();
    }

    @GetMapping("/{id}")
    public Module obtenerModule(@PathVariable Long id) {
        return moduleRepository.findById(id).orElse(null);
    }

    @GetMapping("/course/{courseId}")
    public List<Module> listarModulesPorCurso(@PathVariable Long courseId) {
        return moduleRepository.findByCourseIdOrderByOrder(courseId);
    }

    @PutMapping("/{id}")
    public String actualizarModule(@PathVariable Long id, @RequestBody ModuleDTO dto) {
        Optional<Module> moduleOpt = moduleRepository.findById(id);
        if (moduleOpt.isEmpty()) {
            return "Módulo no encontrado";
        }

        Module module = moduleOpt.get();

        // Si se cambia el curso, verificar que exista
        if (!module.getCourse().getId().equals(dto.getCourseId())) {
            Optional<Course> courseOpt = courseRepository.findById(dto.getCourseId());
            if (courseOpt.isEmpty()) {
                return "Curso no encontrado";
            }
            module.setCourse(courseOpt.get());
        }

        // Verificar si el nuevo orden entra en conflicto con otro módulo del curso
        if (!module.getOrder().equals(dto.getOrder()) &&
                moduleRepository.existsByCourseIdAndOrder(dto.getCourseId(), dto.getOrder())) {
            return "Ya existe un módulo con el orden " + dto.getOrder() + " en este curso";
        }

        module.setTitle(dto.getTitle());
        module.setDescription(dto.getDescription());
        module.setOrder(dto.getOrder());

        moduleRepository.save(module);
        return "Módulo actualizado correctamente";
    }

    @DeleteMapping("/{id}")
    public String eliminarModule(@PathVariable Long id) {
        if (moduleRepository.existsById(id)) {
            moduleRepository.deleteById(id);
            return "Módulo eliminado correctamente";
        } else {
            return "Módulo no encontrado";
        }
    }
}