package com.tecsup.demo.authentication.repository;

import com.tecsup.demo.authentication.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
}
