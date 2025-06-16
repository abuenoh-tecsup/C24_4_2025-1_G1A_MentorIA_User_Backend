package com.tecsup.demo.evaluations.repository;

import com.tecsup.demo.evaluations.model.QuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, Long> {
    List<QuestionAnswer> findByAttemptId(Long attemptId);
    Optional<QuestionAnswer> findByAttemptIdAndQuestionId(Long attemptId, Long questionId);
    boolean existsByAttemptIdAndQuestionId(Long attemptId, Long questionId);
    long countByAttemptId(Long attemptId);
}
