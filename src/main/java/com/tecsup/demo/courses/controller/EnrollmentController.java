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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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
    public ResponseEntity<?> crearEnrollment(@RequestBody EnrollmentDTO dto) {
        Optional<Course> courseOpt = courseRepository.findById(dto.getCourseId());
        if (courseOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Curso no encontrado"));
        }

        Optional<AcademicPeriod> periodOpt = academicPeriodRepository.findById(dto.getPeriodId());
        if (periodOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Período académico no encontrado"));
        }

        Optional<Student> studentOpt = studentRepository.findById(dto.getStudentId());
        if (studentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Estudiante no encontrado"));
        }

        if (enrollmentRepository.existsByStudentIdAndCourseIdAndPeriodId(
                dto.getStudentId(), dto.getCourseId(), dto.getPeriodId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "El estudiante ya está inscrito en este curso para el período seleccionado"));
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStatus(dto.getStatus());
        enrollment.setEnrollmentDate(dto.getEnrollmentDate());
        enrollment.setCourse(courseOpt.get());
        enrollment.setPeriod(periodOpt.get());
        enrollment.setStudent(studentOpt.get());

        enrollmentRepository.save(enrollment);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Inscripción creada correctamente"));
    }

    @GetMapping
    public ResponseEntity<List<Enrollment>> listarEnrollments() {
        return ResponseEntity.ok(enrollmentRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerEnrollment(@PathVariable Long id) {
        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findById(id);
        if (enrollmentOpt.isPresent()) {
            return ResponseEntity.ok(enrollmentOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Inscripción no encontrada"));
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Enrollment>> listarEnrollmentsPorEstudiante(@PathVariable Long studentId) {
        return ResponseEntity.ok(enrollmentRepository.findByStudentId(studentId));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Enrollment>> listarEnrollmentsPorCurso(@PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentRepository.findByCourseId(courseId));
    }

    @GetMapping("/period/{periodId}")
    public ResponseEntity<List<Enrollment>> listarEnrollmentsPorPeriodo(@PathVariable Long periodId) {
        return ResponseEntity.ok(enrollmentRepository.findByPeriodId(periodId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarEnrollment(@PathVariable Long id, @RequestBody EnrollmentDTO dto) {
        Optional<Enrollment> enrollmentOpt = enrollmentRepository.findById(id);
        if (enrollmentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Inscripción no encontrada"));
        }

        Enrollment enrollment = enrollmentOpt.get();

        boolean relationChanged = !enrollment.getStudent().getId().equals(dto.getStudentId()) ||
                !enrollment.getCourse().getId().equals(dto.getCourseId()) ||
                !enrollment.getPeriod().getId().equals(dto.getPeriodId());

        if (relationChanged) {
            if (enrollmentRepository.existsByStudentIdAndCourseIdAndPeriodId(
                    dto.getStudentId(), dto.getCourseId(), dto.getPeriodId())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "Ya existe una inscripción para esta combinación de estudiante, curso y período"));
            }

            Optional<Course> courseOpt = courseRepository.findById(dto.getCourseId());
            if (courseOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Curso no encontrado"));
            }

            Optional<AcademicPeriod> periodOpt = academicPeriodRepository.findById(dto.getPeriodId());
            if (periodOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Período académico no encontrado"));
            }

            Optional<Student> studentOpt = studentRepository.findById(dto.getStudentId());
            if (studentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Estudiante no encontrado"));
            }

            enrollment.setCourse(courseOpt.get());
            enrollment.setPeriod(periodOpt.get());
            enrollment.setStudent(studentOpt.get());
        }

        enrollment.setStatus(dto.getStatus());
        enrollment.setEnrollmentDate(dto.getEnrollmentDate());

        enrollmentRepository.save(enrollment);
        return ResponseEntity.ok(Map.of("message", "Inscripción actualizada correctamente"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarEnrollment(@PathVariable Long id) {
        if (enrollmentRepository.existsById(id)) {
            enrollmentRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Inscripción eliminada correctamente"));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Inscripción no encontrada"));
        }
    }
}
