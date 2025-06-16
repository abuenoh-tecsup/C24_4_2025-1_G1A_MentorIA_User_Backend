package com.tecsup.demo.courses.repository;

import com.tecsup.demo.courses.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    List<Announcement> findByCourseIdOrderByPublicationDateDesc(Long courseId);
    List<Announcement> findByAuthorIdOrderByCreationDateDesc(Long authorId);
}
