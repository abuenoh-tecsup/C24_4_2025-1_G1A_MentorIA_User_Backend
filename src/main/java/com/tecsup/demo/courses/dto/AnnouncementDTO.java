package com.tecsup.demo.courses.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AnnouncementDTO {
    private String title;
    private String content;
    private LocalDateTime publicationDate;
    private Long authorId;
    private Long courseId;
}