package com.qrystal.app.category.dto;

import com.qrystal.app.category.domain.CategoryStatus;
import com.qrystal.app.category.model.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Long id;
    private Long parentId;
    private String name;
    private String description;
    private int level;
    private int ordering;
    private CategoryStatus status;
    private String parentName;  // 상위 카테고리 이름
    private List<CategoryResponse> children;  // 하위 카테고리 목록
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Entity -> DTO 변환 메서드
    public static CategoryResponse from(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .parentId(category.getParentId())
                .name(category.getName())
                .description(category.getDescription())
                .level(category.getLevel())
                .ordering(category.getOrdering())
                .status(category.getStatus())
                .parentName(category.getParentName())
                .children(category.getChildren() != null ?
                        category.getChildren().stream()
                                .map(CategoryResponse::from)
                                .collect(Collectors.toList()) : null)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}