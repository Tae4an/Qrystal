package com.qrystal.app.category.model;

import com.qrystal.app.category.domain.CategoryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    private Long id;
    private Long parentId;
    private String name;
    private String description;
    private int level;
    private int ordering;
    private CategoryStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 계층구조 표현을 위한 추가 필드
    private List<Category> children;
    private String parentName;  // 상위 카테고리 이름
}