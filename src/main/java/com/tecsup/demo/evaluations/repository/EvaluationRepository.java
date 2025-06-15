package com.tecsup.demo.evaluations.repository;

import com.tecsup.demo.evaluations.model.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
}
