package com.tecsup.demo.evaluations.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "question")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "statement", nullable = false, columnDefinition = "LONGTEXT")
    private String statement;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 15)
    private QuestionType type;

    @Column(name = "options", columnDefinition = "JSON")
    private String options; // JSON string

    @Column(name = "correct_answer", nullable = false, columnDefinition = "LONGTEXT")
    private String correctAnswer;

    @ManyToOne
    @JoinColumn(name = "evaluation_id", nullable = false)
    private Evaluation evaluation;

    public enum QuestionType {
        multiple_choice, true_false, short_answer, essay
    }
}
