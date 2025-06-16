package com.tecsup.demo.forums.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ForumDTO {
    private String title;
    private String description;
    private LocalDateTime creationDate;
    private Long authorId;
    private Long moduleId;
}