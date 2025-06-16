package com.tecsup.demo.evaluations.dto;

import lombok.Data;

@Data
public class QuestionDTO {
    private String statement;
    private String type; // multiple_choice, true_false, short_answer, essay
    private String options;
    private String correctAnswer;
    private Long evaluationId;
}