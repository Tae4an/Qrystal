package com.qrystal.app.exam.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CategoryCreateRequest {
    @NotBlank(message = "카테고리명은 필수입니다")
    private String name;
    private Long parentId;  // 최상위 카테고리인 경우 null
}