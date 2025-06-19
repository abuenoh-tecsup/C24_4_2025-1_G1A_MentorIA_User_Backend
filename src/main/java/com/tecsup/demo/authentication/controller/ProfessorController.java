package com.tecsup.demo.authentication.controller;

import com.tecsup.demo.authentication.dto.ProfessorDTO;
import com.tecsup.demo.authentication.model.Professor;
import com.tecsup.demo.authentication.model.User;
import com.tecsup.demo.authentication.repository.ProfessorRepository;
import com.tecsup.demo.authentication.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/professors")
public class ProfessorController {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> crearProfessor(@RequestBody ProfessorDTO dto) {
        Optional<User> userOpt = userRepository.findById(dto.getUserId());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Usuario no encontrado"));
        }

        if (professorRepository.existsByEmployeeCode(dto.getEmployeeCode())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Ya existe un profesor con el código de empleado: " + dto.getEmployeeCode()));
        }

        if (professorRepository.existsByUserId(dto.getUserId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "El usuario ya está asociado a otro profesor"));
        }

        Professor professor = new Professor();
        professor.setEmployeeCode(dto.getEmployeeCode());
        professor.setHireDate(dto.getHireDate());
        professor.setDepartment(dto.getDepartment());
        professor.setAcademicTitle(dto.getAcademicTitle());
        professor.setOfficeLocation(dto.getOfficeLocation());
        professor.setStatus(dto.getStatus());
        professor.setUser(userOpt.get());

        professorRepository.save(professor);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Profesor creado correctamente"));
    }

    @GetMapping
    public ResponseEntity<List<Professor>> listarProfessors() {
        List<Professor> profesores = professorRepository.findAll();
        return ResponseEntity.ok(profesores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerProfessor(@PathVariable Long id) {
        Optional<Professor> professorOpt = professorRepository.findById(id);

        if (professorOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Profesor no encontrado"));
        }

        return ResponseEntity.ok(professorOpt.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarProfessor(@PathVariable Long id, @RequestBody ProfessorDTO dto) {
        Optional<Professor> professorOpt = professorRepository.findById(id);

        if (professorOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Profesor no encontrado"));
        }

        Professor professor = professorOpt.get();

        if (!professor.getEmployeeCode().equals(dto.getEmployeeCode()) &&
                professorRepository.existsByEmployeeCode(dto.getEmployeeCode())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "Ya existe un profesor con el código de empleado: " + dto.getEmployeeCode()));
        }

        if (!professor.getUser().getId().equals(dto.getUserId())) {
            Optional<User> userOpt = userRepository.findById(dto.getUserId());

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Usuario no encontrado"));
            }

            if (professorRepository.existsByUserId(dto.getUserId())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "El usuario ya está asociado a otro profesor"));
            }

            professor.setUser(userOpt.get());
        }

        professor.setEmployeeCode(dto.getEmployeeCode());
        professor.setHireDate(dto.getHireDate());
        professor.setDepartment(dto.getDepartment());
        professor.setAcademicTitle(dto.getAcademicTitle());
        professor.setOfficeLocation(dto.getOfficeLocation());
        professor.setStatus(dto.getStatus());

        professorRepository.save(professor);

        return ResponseEntity.ok(Map.of("message", "Profesor actualizado correctamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProfessor(@PathVariable Long id) {
        if (!professorRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Profesor no encontrado"));
        }

        professorRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Profesor eliminado correctamente"));
    }
}
