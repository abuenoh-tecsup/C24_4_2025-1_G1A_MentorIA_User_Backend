package com.tecsup.demo.forums.repository;

import com.tecsup.demo.forums.model.Forum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForumRepository extends JpaRepository<Forum, Long> {
    List<Forum> findByModuleIdOrderByCreationDateDesc(Long moduleId);
    List<Forum> findByAuthorIdOrderByCreationDateDesc(Long authorId);
    boolean existsByModuleIdAndTitle(Long moduleId, String title);
    long countByModuleId(Long moduleId);
}
