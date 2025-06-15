package com.tecsup.demo.courses.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tecsup.demo.courses.model.Module;

public interface ModuleRepository extends JpaRepository<Module, Long> {
}
