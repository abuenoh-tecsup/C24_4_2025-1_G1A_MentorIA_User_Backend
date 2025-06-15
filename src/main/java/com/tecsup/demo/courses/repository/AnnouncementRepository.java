package com.tecsup.demo.courses.repository;

import com.tecsup.demo.courses.model.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
}
