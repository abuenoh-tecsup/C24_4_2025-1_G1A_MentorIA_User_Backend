package com.tecsup.demo.courses.model;

import com.tecsup.demo.academic.model.AcademicPeriod;
import com.tecsup.demo.authentication.model.Student;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "enrollment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private EnrollmentStatus status;

    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "period_id", nullable = false)
    private AcademicPeriod period;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Table(uniqueConstraints = {
            @UniqueConstraint(columnNames = {"student_id", "course_id", "period_id"})
    })
    public static class EnrollmentConstraints {}

    public enum EnrollmentStatus {
        active, completed, dropped, withdrawn
    }
}
