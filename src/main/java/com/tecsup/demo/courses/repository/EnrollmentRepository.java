package com.tecsup.demo.courses.repository;

import com.tecsup.demo.courses.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByStudentIdAndCourseIdAndPeriodId(Long studentId, Long courseId, Long periodId);
    List<Enrollment> findByStudentId(Long studentId);
    List<Enrollment> findByCourseId(Long courseId);
    List<Enrollment> findByPeriodId(Long periodId);
}
