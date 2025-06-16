package com.tecsup.demo.authentication.controller;

import com.tecsup.demo.authentication.dto.StudentDTO;
import com.tecsup.demo.authentication.model.Student;
import com.tecsup.demo.authentication.model.User;
import com.tecsup.demo.academic.model.Career;
import com.tecsup.demo.authentication.repository.StudentRepository;
import com.tecsup.demo.authentication.repository.UserRepository;
import com.tecsup.demo.academic.repository.CareerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public String crearStudent(@RequestBody StudentDTO dto) {
        // Verificar si el usuario existe
        Optional<User> userOpt = userRepository.findById(dto.getUserId());
        if (userOpt.isEmpty()) {
            return "Usuario no encontrado";
        }

        // Verificar si la carrera existe
        Optional<Career> careerOpt = careerRepository.findById(dto.getCareerId());
        if (careerOpt.isEmpty()) {
            return "Carrera no encontrada";
        }

        // Verificar si ya existe un estudiante con el mismo código
        if (studentRepository.existsByStudentCode(dto.getStudentCode())) {
            return "Ya existe un estudiante con el código: " + dto.getStudentCode();
        }

        // Verificar si el usuario ya está asociado a otro estudiante
        if (studentRepository.existsByUserId(dto.getUserId())) {
            return "El usuario ya está asociado a otro estudiante";
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
        return "Estudiante creado correctamente";
    }

    @GetMapping
    public List<Student> listarStudents() {
        return studentRepository.findAll();
    }

    @GetMapping("/{id}")
    public Student obtenerStudent(@PathVariable Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    @PutMapping("/{id}")
    public String actualizarStudent(@PathVariable Long id, @RequestBody StudentDTO dto) {
        Optional<Student> studentOpt = studentRepository.findById(id);
        if (studentOpt.isEmpty()) {
            return "Estudiante no encontrado";
        }

        Student student = studentOpt.get();

        // Verificar si el nuevo código de estudiante ya existe (y no es el mismo estudiante)
        if (!student.getStudentCode().equals(dto.getStudentCode()) &&
                studentRepository.existsByStudentCode(dto.getStudentCode())) {
            return "Ya existe un estudiante con el código: " + dto.getStudentCode();
        }

        // Si se cambia el usuario, verificar que exista y no esté asociado a otro estudiante
        if (!student.getUser().getId().equals(dto.getUserId())) {
            Optional<User> userOpt = userRepository.findById(dto.getUserId());
            if (userOpt.isEmpty()) {
                return "Usuario no encontrado";
            }

            if (studentRepository.existsByUserId(dto.getUserId())) {
                return "El usuario ya está asociado a otro estudiante";
            }

            student.setUser(userOpt.get());
        }

        // Si se cambia la carrera, verificar que exista
        if (!student.getCareer().getId().equals(dto.getCareerId())) {
            Optional<Career> careerOpt = careerRepository.findById(dto.getCareerId());
            if (careerOpt.isEmpty()) {
                return "Carrera no encontrada";
            }
            student.setCareer(careerOpt.get());
        }

        student.setStudentCode(dto.getStudentCode());
        student.setEnrollmentDate(dto.getEnrollmentDate());
        student.setCurrentSemester(dto.getCurrentSemester());
        student.setAverageGrade(dto.getAverageGrade());
        student.setStatus(dto.getStatus());

        studentRepository.save(student);
        return "Estudiante actualizado correctamente";
    }

    @DeleteMapping("/{id}")
    public String eliminarStudent(@PathVariable Long id) {
        if (studentRepository.existsById(id)) {
            studentRepository.deleteById(id);
            return "Estudiante eliminado correctamente";
        } else {
            return "Estudiante no encontrado";
        }
    }
}