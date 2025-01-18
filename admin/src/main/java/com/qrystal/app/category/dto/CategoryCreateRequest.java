package com.qrystal.app.category.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryCreateRequest {
    private String name;
    private Long parentId;
    private String description;
    private String status;
}