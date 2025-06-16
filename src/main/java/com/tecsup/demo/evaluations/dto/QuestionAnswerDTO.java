package com.tecsup.demo.evaluations.dto;

import lombok.Data;

@Data
public class QuestionAnswerDTO {
    private String answer;
    private Boolean correct;
    private Long attemptId;
    private Long questionId;
}