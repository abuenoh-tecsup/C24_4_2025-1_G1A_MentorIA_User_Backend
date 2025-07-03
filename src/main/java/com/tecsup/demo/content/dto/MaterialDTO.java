package com.tecsup.demo.content.dto;

import com.tecsup.demo.content.model.Material.MaterialType;
import lombok.Data;

@Data
public class MaterialDTO {
    private String title;
    private String description;
    private MaterialType type;
    private Long moduleId;
}
