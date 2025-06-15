package com.tecsup.demo.evaluations.repository;

import com.tecsup.demo.evaluations.model.EvaluationAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationAttemptRepository extends JpaRepository<EvaluationAttempt, Long> {
}
