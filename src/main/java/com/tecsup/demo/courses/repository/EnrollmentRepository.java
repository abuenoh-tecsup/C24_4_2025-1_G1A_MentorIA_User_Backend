package com.tecsup.demo.courses.repository;

import com.tecsup.demo.courses.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
}
