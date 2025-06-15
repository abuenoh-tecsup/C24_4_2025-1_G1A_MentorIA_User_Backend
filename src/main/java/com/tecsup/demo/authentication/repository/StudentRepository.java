package com.tecsup.demo.authentication.repository;

import com.tecsup.demo.authentication.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
}
