package com.tecsup.demo.courses.repository;

import com.tecsup.demo.courses.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
