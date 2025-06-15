package com.tecsup.demo.forums.repository;

import com.tecsup.demo.forums.model.Forum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForumRepository extends JpaRepository<Forum, Long> {
}
