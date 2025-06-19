package com.tecsup.demo.courses.dto;

import lombok.Data;

@Data
public class ModuleDTO {
    private String title;
    private String description;
    private Integer moduleOrder;
    private Long courseId;
}