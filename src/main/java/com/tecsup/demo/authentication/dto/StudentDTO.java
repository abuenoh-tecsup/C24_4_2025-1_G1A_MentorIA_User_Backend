package com.tecsup.demo.authentication.dto;

import com.tecsup.demo.authentication.model.Student.StudentStatus;
import lombok.Data;
import java.time.LocalDate;

@Data
public class StudentDTO {
    private String studentCode;
    private LocalDate enrollmentDate;
    private Integer currentSemester;
    private Double averageGrade;
    private StudentStatus status;
    private Long careerId;
    private Long userId;
}