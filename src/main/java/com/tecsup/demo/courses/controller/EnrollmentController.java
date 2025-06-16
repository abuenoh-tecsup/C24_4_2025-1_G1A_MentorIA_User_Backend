package com.tecsup.demo.courses.controller;

import com.tecsup.demo.courses.dto.EnrollmentDTO;
import com.tecsup.demo.courses.model.Enrollment;
import com.tecsup.demo.courses.model.Course;
import com.tecsup.demo.academic.model.AcademicPeriod;
import com.tecsup.demo.authentication.model.Student;
import com.tecsup.demo.courses.repository.EnrollmentRepository;
import com.tecsup.demo.courses.repository.CourseRepository;
import com.tecsup.demo.academic.repository.AcademicPeriodRepository;
import com.tecsup.demo.authentication.repository.StudentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private AcademicPeriodRepository academicPeriodRepository;

    @Autowired
    private StudentRepository studentRepository;

    @PostMapping
    public String crearEnrollment(@RequestBody EnrollmentDTO dto) {
        // Verificar si el curso existe
        Optional<Course> courseOpt = courseRepository.findById(dto.getCourseId());
        if (courseOpt.isEmpty()) {
            return "Curso no encontrado";
        }

        // Verificar si el período académico existe
        Optional<AcademicPeriod> periodOpt = academicPeriodRepository.findById(dto.getPeriodId());
        if (periodOpt.isEmpty()) {
            return "Período académico no encontrado";
        }

        // Verificar si el estudiante existe
        Optional<Student> studentOpt = studentRepository.findById(dto.getStudentId());
        if (studentOpt.isEmpty()) {
            return "Estudiante no encontrado";
        }

        // Verificar si ya existe una inscripción para este estudiante, curso y período
        if (enrollmentRepository.existsByStudentIdAndCourseIdAndPeriodId(
                dto.getStudentId(), dto.getCourseId(), dto.getPeriodId())) {
            return "El estudiante ya está inscrito en este curso para el período seleccionado";
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStatus(dto.getStatus());
        enrollment.setEnrollmentDate(dto.getEnrollmentDate());
        enrollment.setCourse(courseOpt.get());
        enrollment.setPeriod(periodOpt.get());
        enrollment.setStudent(studentOpt.get());

        enrollmentRepository.save(enrollment);
        return "Inscripción creada correctamente";
    }

    @GetMapping
    public List<Enrollment> listarEnrollments() {
        return enrollmentRepository.findAll();
    }

    @GetMapping("/{id}")
    public Enrollment obtenerEnrollment(@PathVariable Long id) {
        return enrollmentRepository.findById(id).orElse(null);
    }

    @GetMapping("/student/{studentId}")
    public List<Enrollment> listarEnrollmentsPorEstudiante(@PathVariable Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    @GetMapping("/course/{courseId}")
    public List<Enrollment> listarEnrollmentsPorCurso(@PathVariable Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    @GetMapping("/period/{periodId}")
    public List<Enrollment> listarEnrollmentsPorPeriodo(@PathVariable Long periodId) {
        return enrollmentRepository.findByPeriodId(periodId);
    }

    @PutMapping("/{id}")
    public String actualizarEnrollment(@PathVariable Long id, @RequestBody EnrollmentDTO dto) {
        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findById(id);
        if (enrollmentOpt.isEmpty()) {
            return "Inscripción no encontrada";
        }

        Enrollment enrollment = enrollmentOpt.get();

        // Verificar si se cambian las relaciones y si generan conflictos
        boolean relationChanged = !enrollment.getStudent().getId().equals(dto.getStudentId()) ||
                !enrollment.getCourse().getId().equals(dto.getCourseId()) ||
                !enrollment.getPeriod().getId().equals(dto.getPeriodId());

        if (relationChanged) {
            // Verificar si ya existe una inscripción con la nueva combinación
            if (enrollmentRepository.existsByStudentIdAndCourseIdAndPeriodId(
                    dto.getStudentId(), dto.getCourseId(), dto.getPeriodId())) {
                return "Ya existe una inscripción para esta combinación de estudiante, curso y período";
            }

            // Validar que las nuevas entidades existan
            Optional<Course> courseOpt = courseRepository.findById(dto.getCourseId());
            if (courseOpt.isEmpty()) {
                return "Curso no encontrado";
            }

            Optional<AcademicPeriod> periodOpt = academicPeriodRepository.findById(dto.getPeriodId());
            if (periodOpt.isEmpty()) {
                return "Período académico no encontrado";
            }

            Optional<Student> studentOpt = studentRepository.findById(dto.getStudentId());
            if (studentOpt.isEmpty()) {
                return "Estudiante no encontrado";
            }

            enrollment.setCourse(courseOpt.get());
            enrollment.setPeriod(periodOpt.get());
            enrollment.setStudent(studentOpt.get());
        }

        enrollment.setStatus(dto.getStatus());
        enrollment.setEnrollmentDate(dto.getEnrollmentDate());

        enrollmentRepository.save(enrollment);
        return "Inscripción actualizada correctamente";
    }

    @DeleteMapping("/{id}")
    public String eliminarEnrollment(@PathVariable Long id) {
        if (enrollmentRepository.existsById(id)) {
            enrollmentRepository.deleteById(id);
            return "Inscripción eliminada correctamente";
        } else {
            return "Inscripción no encontrada";
        }
    }
}