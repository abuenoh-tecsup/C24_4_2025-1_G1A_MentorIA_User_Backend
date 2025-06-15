package com.tecsup.demo.content.repository;

import com.tecsup.demo.content.model.FavoriteMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteMaterialRepository extends JpaRepository<FavoriteMaterial, Long> {
}
