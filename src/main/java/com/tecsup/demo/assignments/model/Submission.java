package com.tecsup.demo.assignments.model;

import com.tecsup.demo.authentication.model.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "submission")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "submission_date", nullable = false)
    private LocalDateTime submissionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private SubmissionStatus status;

    @Column(name = "grade")
    private Double grade;

    @Column(name = "comments", columnDefinition = "LONGTEXT")
    private String comments;

    @Column(name = "file_url", length = 200)
    private String fileUrl;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Table(uniqueConstraints = {
            @UniqueConstraint(columnNames = {"task_id", "user_id"})
    })
    public static class SubmissionConstraints {}

    public enum SubmissionStatus {
        submitted, graded, pending, late, empty
    }
}
