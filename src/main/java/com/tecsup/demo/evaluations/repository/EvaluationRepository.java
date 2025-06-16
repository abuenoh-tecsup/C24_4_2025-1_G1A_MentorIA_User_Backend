package com.tecsup.demo.evaluations.repository;

import com.tecsup.demo.evaluations.model.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findByModuleIdOrderByStartDate(Long moduleId);
    boolean existsByModuleIdAndTitle(Long moduleId, String title);
}
