package com.tecsup.demo.evaluations.model;

import com.tecsup.demo.authentication.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "evaluation_attempt")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "submission_date", nullable = false)
    private LocalDateTime submissionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AttemptStatus status;

    @Column(name = "score")
    private Double score;

    @Column(name = "comments", columnDefinition = "LONGTEXT")
    private String comments;

    @ManyToOne
    @JoinColumn(name = "evaluation_id", nullable = false)
    private Evaluation evaluation;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public enum AttemptStatus {
        submitted, graded, in_progress, not_started
    }
}
