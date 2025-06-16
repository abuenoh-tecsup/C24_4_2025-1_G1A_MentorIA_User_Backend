package com.tecsup.demo.assignments.dto;

import com.tecsup.demo.assignments.model.Submission.SubmissionStatus;
import lombok.Data;

@Data
public class SubmissionDTO {
    private Long userId;
    private Long taskId;
    private SubmissionStatus status;
    private Double grade;
    private String comments;
    private String fileUrl;
    // submissionDate se asigna automáticamente
}