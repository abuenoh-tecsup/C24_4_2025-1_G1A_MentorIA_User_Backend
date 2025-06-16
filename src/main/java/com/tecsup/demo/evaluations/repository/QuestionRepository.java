package com.tecsup.demo.evaluations.repository;

import com.tecsup.demo.evaluations.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByEvaluationIdOrderById(Long evaluationId);
    long countByEvaluationId(Long evaluationId);
}
