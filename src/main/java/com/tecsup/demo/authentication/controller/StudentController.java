package com.tecsup.demo.authentication.controller;

import com.tecsup.demo.authentication.dto.StudentDTO;
import com.tecsup.demo.authentication.model.Student;
import com.tecsup.demo.authentication.model.User;
import com.tecsup.demo.academic.model.Career;
import com.tecsup.demo.authentication.repository.StudentRepository;
import com.tecsup.demo.authentication.repository.UserRepository;
import com.tecsup.demo.academic.repository.CareerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CareerRepository careerRepository;

    @PostMapping
    public ResponseEntity<?> crearStudent(@RequestBody StudentDTO dto) {
        Optional<User> userOpt = userRepository.findById(dto.getUserId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Usuario no encontrado"));
        }

        Optional<Career> careerOpt = careerRepository.findById(dto.getCareerId());
        if (careerOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Carrera no encontrada"));
        }

        if (studentRepository.existsByStudentCode(dto.getStudentCode())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Ya existe un estudiante con el código: " + dto.getStudentCode()));
        }

        if (studentRepository.existsByUserId(dto.getUserId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "El usuario ya está asociado a otro estudiante"));
        }

        Student student = new Student();
        student.setStudentCode(dto.getStudentCode());
        student.setEnrollmentDate(dto.getEnrollmentDate());
        student.setCurrentSemester(dto.getCurrentSemester());
        student.setAverageGrade(dto.getAverageGrade());
        student.setStatus(dto.getStatus());
        student.setCareer(careerOpt.get());
        student.setUser(userOpt.get());

        studentRepository.save(student);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Estudiante creado correctamente"));
    }

    @GetMapping
    public ResponseEntity<List<Student>> listarStudents() {
        List<Student> estudiantes = studentRepository.findAll();
        return ResponseEntity.ok(estudiantes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerStudent(@PathVariable Long id) {
        Optional<Student> studentOpt = studentRepository.findById(id);

        if (studentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Estudiante no encontrado"));
        }

        return ResponseEntity.ok(studentOpt.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarStudent(@PathVariable Long id, @RequestBody StudentDTO dto) {
        Optional<Student> studentOpt = studentRepository.findById(id);

        if (studentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Estudiante no encontrado"));
        }

        Student student = studentOpt.get();

        if (!student.getStudentCode().equals(dto.getStudentCode()) &&
                studentRepository.existsByStudentCode(dto.getStudentCode())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Ya existe un estudiante con el código: " + dto.getStudentCode()));
        }

        if (!student.getUser().getId().equals(dto.getUserId())) {
            Optional<User> userOpt = userRepository.findById(dto.getUserId());
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Usuario no encontrado"));
            }

            if (studentRepository.existsByUserId(dto.getUserId())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "El usuario ya está asociado a otro estudiante"));
            }

            student.setUser(userOpt.get());
        }

        if (!student.getCareer().getId().equals(dto.getCareerId())) {
            Optional<Career> careerOpt = careerRepository.findById(dto.getCareerId());
            if (careerOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Carrera no encontrada"));
            }

            student.setCareer(careerOpt.get());
        }

        student.setStudentCode(dto.getStudentCode());
        student.setEnrollmentDate(dto.getEnrollmentDate());
        student.setCurrentSemester(dto.getCurrentSemester());
        student.setAverageGrade(dto.getAverageGrade());
        student.setStatus(dto.getStatus());

        studentRepository.save(student);
        return ResponseEntity.ok(Map.of("message", "Estudiante actualizado correctamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarStudent(@PathVariable Long id) {
        if (!studentRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Estudiante no encontrado"));
        }

        studentRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Estudiante eliminado correctamente"));
    }
}
