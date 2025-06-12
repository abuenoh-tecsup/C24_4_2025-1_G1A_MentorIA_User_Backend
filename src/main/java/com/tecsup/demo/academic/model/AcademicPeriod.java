package com.tecsup.demo.academic.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "academic_period")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcademicPeriod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "term", nullable = false)
    private Integer term;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Table(uniqueConstraints = {
            @UniqueConstraint(columnNames = {"year", "term"})
    })
    public static class AcademicPeriodConstraints {}
}
