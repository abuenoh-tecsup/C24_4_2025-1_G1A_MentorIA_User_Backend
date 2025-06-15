package com.tecsup.demo.evaluations.repository;

import com.tecsup.demo.evaluations.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Long> {
}
