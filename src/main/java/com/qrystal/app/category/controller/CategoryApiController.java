package com.qrystal.app.category.controller;

import com.qrystal.app.category.dto.CategoryResponse;
import com.qrystal.app.category.model.Category;
import com.qrystal.app.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "카테고리 관리", description = "카테고리 관련 API")
public class CategoryApiController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "활성화된 카테고리 조회", description = "현재 활성화된 모든 카테고리를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 카테고리 목록을 반환함",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CategoryResponse.class)))
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        // 활성화된 카테고리만 조회
        List<Category> categories = categoryService.getActiveCategories();
        List<CategoryResponse> response = categories.stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}