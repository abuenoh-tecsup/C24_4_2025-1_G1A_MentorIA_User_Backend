package com.tecsup.demo.evaluations.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EvaluationAttemptDTO {
    private LocalDateTime submissionDate;
    private String status; // submitted, graded, in_progress, not_started
    private Double score;
    private String comments;
    private Long evaluationId;
    private Long userId;
}