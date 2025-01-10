package com.qrystal.app.exam.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Category {
    private Long id;
    private String name;
    private Long parentId;
    private Integer depth;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}