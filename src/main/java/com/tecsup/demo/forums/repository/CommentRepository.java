package com.tecsup.demo.forums.repository;

import com.tecsup.demo.forums.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByForumIdOrderByCreationDate(Long forumId);
    List<Comment> findByUserIdOrderByCreationDateDesc(Long userId);
    long countByForumId(Long forumId);
    long countByUserId(Long userId);
}
