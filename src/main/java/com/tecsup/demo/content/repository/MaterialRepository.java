package com.tecsup.demo.content.repository;

import com.tecsup.demo.content.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<Material, Long> {
}
