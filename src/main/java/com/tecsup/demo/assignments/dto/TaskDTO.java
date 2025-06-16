package com.tecsup.demo.assignments.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskDTO {
    private String title;
    private String description;
    private LocalDateTime publicationDate;
    private LocalDateTime dueDate;
    private Long moduleId;
}