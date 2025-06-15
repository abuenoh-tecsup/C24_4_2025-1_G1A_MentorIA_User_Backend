package com.tecsup.demo.forums.repository;

import com.tecsup.demo.forums.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
