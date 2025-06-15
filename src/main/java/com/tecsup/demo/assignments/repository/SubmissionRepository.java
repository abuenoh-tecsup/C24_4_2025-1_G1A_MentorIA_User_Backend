package com.tecsup.demo.assignments.repository;

import com.tecsup.demo.assignments.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
}
