package com.tecsup.demo.courses.repository;

import com.tecsup.demo.courses.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByProfessorId(Long professorId);
    List<Course> findBySubjectId(Long subjectId);
}
