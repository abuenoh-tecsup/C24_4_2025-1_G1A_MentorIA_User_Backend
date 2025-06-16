package com.tecsup.demo.authentication.controller;

import com.tecsup.demo.authentication.dto.ProfessorDTO;
import com.tecsup.demo.authentication.model.Professor;
import com.tecsup.demo.authentication.model.User;
import com.tecsup.demo.authentication.repository.ProfessorRepository;
import com.tecsup.demo.authentication.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/professors")
public class ProfessorController {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public String crearProfessor(@RequestBody ProfessorDTO dto) {
        // Verificar si el usuario existe
        Optional<User> userOpt = userRepository.findById(dto.getUserId());
        if (userOpt.isEmpty()) {
            return "Usuario no encontrado";
        }

        // Verificar si ya existe un profesor con el mismo código de empleado
        if (professorRepository.existsByEmployeeCode(dto.getEmployeeCode())) {
            return "Ya existe un profesor con el código de empleado: " + dto.getEmployeeCode();
        }

        // Verificar si el usuario ya está asociado a otro profesor
        if (professorRepository.existsByUserId(dto.getUserId())) {
            return "El usuario ya está asociado a otro profesor";
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
        return "Profesor creado correctamente";
    }

    @GetMapping
    public List<Professor> listarProfessors() {
        return professorRepository.findAll();
    }

    @GetMapping("/{id}")
    public Professor obtenerProfessor(@PathVariable Long id) {
        return professorRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public String actualizarProfessor(@PathVariable Long id, @RequestBody ProfessorDTO dto) {
        Optional<Professor> professorOpt = professorRepository.findById(id);
        if (professorOpt.isEmpty()) {
            return "Profesor no encontrado";
        }

        Professor professor = professorOpt.get();

        // Verificar si el nuevo código de empleado ya existe (y no es el mismo profesor)
        if (!professor.getEmployeeCode().equals(dto.getEmployeeCode()) &&
                professorRepository.existsByEmployeeCode(dto.getEmployeeCode())) {
            return "Ya existe un profesor con el código de empleado: " + dto.getEmployeeCode();
        }

        // Si se cambia el usuario, verificar que exista y no esté asociado a otro profesor
        if (!professor.getUser().getId().equals(dto.getUserId())) {
            Optional<User> userOpt = userRepository.findById(dto.getUserId());
            if (userOpt.isEmpty()) {
                return "Usuario no encontrado";
            }

            if (professorRepository.existsByUserId(dto.getUserId())) {
                return "El usuario ya está asociado a otro profesor";
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
        return "Profesor actualizado correctamente";
    }

    @DeleteMapping("/{id}")
    public String eliminarProfessor(@PathVariable Long id) {
        if (professorRepository.existsById(id)) {
            professorRepository.deleteById(id);
            return "Profesor eliminado correctamente";
        } else {
            return "Profesor no encontrado";
        }
    }
}