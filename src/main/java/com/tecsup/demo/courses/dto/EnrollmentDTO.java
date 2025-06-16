package com.tecsup.demo.courses.dto;

import com.tecsup.demo.courses.model.Enrollment.EnrollmentStatus;
import lombok.Data;
import java.time.LocalDate;

@Data
public class EnrollmentDTO {
    private EnrollmentStatus status;
    private LocalDate enrollmentDate;
    private Long courseId;
    private Long periodId;
    private Long studentId;
}