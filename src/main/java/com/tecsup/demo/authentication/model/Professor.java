package com.tecsup.demo.authentication.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "professor")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Professor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_code", nullable = false, unique = true, length = 20)
    private String employeeCode;

    @Column(name = "hire_date", nullable = false)
    private java.time.LocalDate hireDate;

    @Column(name = "department", nullable = false, length = 100)
    private String department;

    @Column(name = "academic_title", nullable = false, length = 100)
    private String academicTitle;

    @Column(name = "office_location", length = 100)
    private String officeLocation;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private ProfessorStatus status;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    public enum ProfessorStatus {
        active, inactive, retired
    }
}
