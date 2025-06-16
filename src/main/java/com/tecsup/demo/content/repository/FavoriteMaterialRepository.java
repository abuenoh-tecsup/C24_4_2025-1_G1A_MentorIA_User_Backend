package com.tecsup.demo.content.repository;

import com.tecsup.demo.content.model.FavoriteMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteMaterialRepository extends JpaRepository<FavoriteMaterial, Long> {
    // Verificar si ya existe el favorito (para evitar duplicados)
    boolean existsByUserIdAndMaterialId(Long userId, Long materialId);

    // Obtener favoritos de un usuario ordenados por fecha (más recientes primero)
    List<FavoriteMaterial> findByUserIdOrderByDateDesc(Long userId);

    // Obtener usuarios que marcaron como favorito un material específico
    List<FavoriteMaterial> findByMaterialIdOrderByDateDesc(Long materialId);

    // Encontrar favorito específico por usuario y material
    Optional<FavoriteMaterial> findByUserIdAndMaterialId(Long userId, Long materialId);

    // Contar favoritos de un usuario
    long countByUserId(Long userId);

    // Contar cuántos usuarios marcaron como favorito un material
    long countByMaterialId(Long materialId);
}
