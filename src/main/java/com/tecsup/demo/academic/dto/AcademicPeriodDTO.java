package com.tecsup.demo.academic.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AcademicPeriodDTO {
    private String name;
    private Integer year;
    private Integer term;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}