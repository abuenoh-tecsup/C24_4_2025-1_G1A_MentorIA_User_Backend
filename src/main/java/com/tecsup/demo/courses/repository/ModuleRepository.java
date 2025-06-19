package com.tecsup.demo.courses.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tecsup.demo.courses.model.Module;

import java.util.List;

public interface ModuleRepository extends JpaRepository<Module, Long> {
    boolean existsByCourseIdAndModuleOrder(Long courseId, Integer order);
    List<Module> findByCourseIdOrderByModuleOrder(Long courseId);
}
