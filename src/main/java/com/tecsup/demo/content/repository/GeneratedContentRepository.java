package com.tecsup.demo.content.repository;

import com.tecsup.demo.content.model.GeneratedContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GeneratedContentRepository extends JpaRepository<GeneratedContent, Long> {
    List<GeneratedContent> findByUserId(Long userId);
    List<GeneratedContent> findByMaterialId(Long materialId);

}
