package com.tecsup.demo.assignments.repository;

import com.tecsup.demo.assignments.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    boolean existsByUserIdAndTaskId(Long userId, Long taskId);

    List<Submission> findByTaskIdOrderBySubmissionDateDesc(Long taskId);

    List<Submission> findByUserIdOrderBySubmissionDateDesc(Long userId);
}
