package com.tecsup.demo.academic.repository;

import com.tecsup.demo.academic.model.AcademicPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademicPeriodRepository extends JpaRepository<AcademicPeriod, Long> {
}
