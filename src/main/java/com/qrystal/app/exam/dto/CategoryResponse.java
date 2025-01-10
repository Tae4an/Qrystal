package com.qrystal.app.exam.dto;

import com.qrystal.app.exam.model.Category;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private Long parentId;
    private Integer depth;
    private LocalDateTime createdAt;
    
    public static CategoryResponse of(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .parentId(category.getParentId())
                .depth(category.getDepth())
                .createdAt(category.getCreatedAt())
                .build();
    }
}