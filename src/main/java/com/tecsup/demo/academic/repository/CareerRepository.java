package com.tecsup.demo.academic.repository;

import com.tecsup.demo.academic.model.Career;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareerRepository extends JpaRepository<Career, Long> {
}
