package com.tecsup.demo.authentication.model;

import com.tecsup.demo.academic.model.Career;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "student")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_code", nullable = false, unique = true, length = 20)
    private String studentCode;

    @Column(name = "enrollment_date", nullable = false)
    private java.time.LocalDate enrollmentDate;

    @Column(name = "current_semester", nullable = false)
    private Integer currentSemester;

    @Column(name = "average_grade", nullable = false)
    private Double averageGrade;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private StudentStatus status;

    @ManyToOne
    @JoinColumn(name = "career_id", nullable = false)
    private Career career;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public enum StudentStatus {
        active, inactive, graduated, suspended
    }
}