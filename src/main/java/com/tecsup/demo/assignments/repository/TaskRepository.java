package com.tecsup.demo.assignments.repository;

import com.tecsup.demo.assignments.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
