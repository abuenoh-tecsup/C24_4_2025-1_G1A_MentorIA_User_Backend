package com.tecsup.demo.evaluations.repository;

import com.tecsup.demo.evaluations.model.EvaluationAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EvaluationAttemptRepository extends JpaRepository<EvaluationAttempt, Long> {
    List<EvaluationAttempt> findByEvaluationIdOrderBySubmissionDate(Long evaluationId);
    List<EvaluationAttempt> findByUserIdOrderBySubmissionDate(Long userId);
    Optional<EvaluationAttempt> findByEvaluationIdAndUserId(Long evaluationId, Long userId);
    boolean existsByEvaluationIdAndUserId(Long evaluationId, Long userId);
}
