package com.tecsup.demo.courses.controller;

import com.tecsup.demo.courses.dto.CourseDTO;
import com.tecsup.demo.courses.model.Course;
import com.tecsup.demo.authentication.model.User;
import com.tecsup.demo.academic.model.Subject;
import com.tecsup.demo.courses.repository.CourseRepository;
import com.tecsup.demo.authentication.repository.UserRepository;
import com.tecsup.demo.academic.repository.SubjectRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @PostMapping
    public String crearCourse(@RequestBody CourseDTO dto) {
        // Verificar si el profesor (usuario) existe
        Optional<User> professorOpt = userRepository.findById(dto.getProfessorId());
        if (professorOpt.isEmpty()) {
            return "Profesor no encontrado";
        }

        // Verificar si el usuario es realmente un profesor
        User professor = professorOpt.get();
        if (!professor.getRole().equals(User.UserRole.professor)) {
            return "El usuario seleccionado no es un profesor";
        }

        // Verificar si la materia existe
        Optional<Subject> subjectOpt = subjectRepository.findById(dto.getSubjectId());
        if (subjectOpt.isEmpty()) {
            return "Materia no encontrada";
        }

        Course course = new Course();
        course.setProfessor(professor);
        course.setSubject(subjectOpt.get());

        courseRepository.save(course);
        return "Curso creado correctamente";
    }

    @GetMapping
    public List<Course> listarCourses() {
        return courseRepository.findAll();
    }

    @GetMapping("/{id}")
    public Course obtenerCourse(@PathVariable Long id) {
        return courseRepository.findById(id).orElse(null);
    }

    @GetMapping("/professor/{professorId}")
    public List<Course> listarCoursesPorProfesor(@PathVariable Long professorId) {
        return courseRepository.findByProfessorId(professorId);
    }

    @GetMapping("/subject/{subjectId}")
    public List<Course> listarCoursesPorMateria(@PathVariable Long subjectId) {
        return courseRepository.findBySubjectId(subjectId);
    }

    @PutMapping("/{id}")
    public String actualizarCourse(@PathVariable Long id, @RequestBody CourseDTO dto) {
        Optional<Course> courseOpt = courseRepository.findById(id);
        if (courseOpt.isEmpty()) {
            return "Curso no encontrado";
        }

        Course course = courseOpt.get();

        // Si se cambia el profesor, verificar que exista y sea realmente un profesor
        if (!course.getProfessor().getId().equals(dto.getProfessorId())) {
            Optional<User> professorOpt = userRepository.findById(dto.getProfessorId());
            if (professorOpt.isEmpty()) {
                return "Profesor no encontrado";
            }

            User professor = professorOpt.get();
            if (!professor.getRole().equals(User.UserRole.professor)) {
                return "El usuario seleccionado no es un profesor";
            }

            course.setProfessor(professor);
        }

        // Si se cambia la materia, verificar que exista
        if (!course.getSubject().getId().equals(dto.getSubjectId())) {
            Optional<Subject> subjectOpt = subjectRepository.findById(dto.getSubjectId());
            if (subjectOpt.isEmpty()) {
                return "Materia no encontrada";
            }
            course.setSubject(subjectOpt.get());
        }

        courseRepository.save(course);
        return "Curso actualizado correctamente";
    }

    @DeleteMapping("/{id}")
    public String eliminarCourse(@PathVariable Long id) {
        if (courseRepository.existsById(id)) {
            courseRepository.deleteById(id);
            return "Curso eliminado correctamente";
        } else {
            return "Curso no encontrado";
        }
    }
}