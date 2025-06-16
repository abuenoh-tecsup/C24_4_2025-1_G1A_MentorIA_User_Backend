package com.tecsup.demo.evaluations.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EvaluationDTO {
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer timeLimit;
    private Long moduleId;
}