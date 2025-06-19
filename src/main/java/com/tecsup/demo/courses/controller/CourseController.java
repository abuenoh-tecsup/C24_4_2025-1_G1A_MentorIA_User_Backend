package com.tecsup.demo.courses.controller;

import com.tecsup.demo.courses.dto.CourseDTO;
import com.tecsup.demo.courses.model.Course;
import com.tecsup.demo.authentication.model.User;
import com.tecsup.demo.academic.model.Subject;
import com.tecsup.demo.courses.repository.CourseRepository;
import com.tecsup.demo.authentication.repository.UserRepository;
import com.tecsup.demo.academic.repository.SubjectRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
    public ResponseEntity<?> crearCourse(@RequestBody CourseDTO dto) {
        Optional<User> professorOpt = userRepository.findById(dto.getProfessorId());
        if (professorOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Profesor no encontrado"));
        }

        User professor = professorOpt.get();
        if (!professor.getRole().equals(User.UserRole.professor)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "El usuario seleccionado no es un profesor"));
        }

        Optional<Subject> subjectOpt = subjectRepository.findById(dto.getSubjectId());
        if (subjectOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Materia no encontrada"));
        }

        Course course = new Course();
        course.setProfessor(professor);
        course.setSubject(subjectOpt.get());

        courseRepository.save(course);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Curso creado correctamente"));
    }

    @GetMapping
    public ResponseEntity<List<Course>> listarCourses() {
        return ResponseEntity.ok(courseRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCourse(@PathVariable Long id) {
        Optional<Course> courseOpt = courseRepository.findById(id);
        if (courseOpt.isPresent()) {
            return ResponseEntity.ok(courseOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Curso no encontrado"));
        }
    }

    @GetMapping("/professor/{professorId}")
    public ResponseEntity<List<Course>> listarCoursesPorProfesor(@PathVariable Long professorId) {
        return ResponseEntity.ok(courseRepository.findByProfessorId(professorId));
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<Course>> listarCoursesPorMateria(@PathVariable Long subjectId) {
        return ResponseEntity.ok(courseRepository.findBySubjectId(subjectId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCourse(@PathVariable Long id, @RequestBody CourseDTO dto) {
        Optional<Course> courseOpt = courseRepository.findById(id);
        if (courseOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Curso no encontrado"));
        }

        Course course = courseOpt.get();

        if (!course.getProfessor().getId().equals(dto.getProfessorId())) {
            Optional<User> professorOpt = userRepository.findById(dto.getProfessorId());
            if (professorOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Profesor no encontrado"));
            }

            User professor = professorOpt.get();
            if (!professor.getRole().equals(User.UserRole.professor)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "El usuario seleccionado no es un profesor"));
            }

            course.setProfessor(professor);
        }

        if (!course.getSubject().getId().equals(dto.getSubjectId())) {
            Optional<Subject> subjectOpt = subjectRepository.findById(dto.getSubjectId());
            if (subjectOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Materia no encontrada"));
            }

            course.setSubject(subjectOpt.get());
        }

        courseRepository.save(course);

        return ResponseEntity.ok(Map.of("message", "Curso actualizado correctamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCourse(@PathVariable Long id) {
        if (courseRepository.existsById(id)) {
            courseRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Curso eliminado correctamente"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Curso no encontrado"));
        }
    }
}
