package com.tecsup.demo.academic.repository;

import com.tecsup.demo.academic.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
}
