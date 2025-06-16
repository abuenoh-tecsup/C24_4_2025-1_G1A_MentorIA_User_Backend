package com.tecsup.demo.forums.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDTO {
    private String content;
    private LocalDateTime creationDate;
    private Long userId;
    private Long forumId;
}