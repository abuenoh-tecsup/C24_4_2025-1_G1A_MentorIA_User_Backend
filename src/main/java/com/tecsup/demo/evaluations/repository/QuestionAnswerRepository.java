package com.tecsup.demo.evaluations.repository;

import com.tecsup.demo.evaluations.model.QuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswer, Long> {
}
