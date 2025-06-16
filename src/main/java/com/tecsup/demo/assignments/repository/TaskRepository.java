package com.tecsup.demo.assignments.repository;

import com.tecsup.demo.assignments.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByModuleIdOrderByDueDateAsc(Long moduleId);
}
