package com.tecsup.demo.authentication.dto;

import com.tecsup.demo.authentication.model.Professor.ProfessorStatus;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ProfessorDTO {
    private String employeeCode;
    private LocalDate hireDate;
    private String department;
    private String academicTitle;
    private String officeLocation;
    private ProfessorStatus status;
    private Long userId;
}