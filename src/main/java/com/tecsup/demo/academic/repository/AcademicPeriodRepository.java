package com.tecsup.demo.academic.repository;

import com.tecsup.demo.academic.model.AcademicPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AcademicPeriodRepository extends JpaRepository<AcademicPeriod, Long> {
    Optional<AcademicPeriod> findByYearAndTerm(Integer year, Integer term);
}
