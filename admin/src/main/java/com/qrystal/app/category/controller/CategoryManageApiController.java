package com.qrystal.app.category.controller;

import com.qrystal.app.category.dto.CategoryCreateRequest;
import com.qrystal.app.category.dto.CategoryResponse;
import com.qrystal.app.category.dto.CategoryUpdateRequest;
import com.qrystal.app.category.model.Category;
import com.qrystal.app.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryManageApiController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        log.info("조회된 카테고리: {}", categories);
        List<CategoryResponse> response = categories.stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody @Valid CategoryCreateRequest request) {
        Category created = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CategoryResponse.from(created));
    }
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable Long id,
            @RequestBody @Valid CategoryUpdateRequest request) {
        categoryService.updateCategory(id, request);
        Category updated = categoryService.findById(id);
        return ResponseEntity.ok(CategoryResponse.from(updated));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/order/{newOrder}")
    public ResponseEntity<Void> updateCategoryOrder(
            @PathVariable Long id,
            @PathVariable @Min(0) int newOrder) {
        categoryService.updateCategoryOrder(id, newOrder);
        return ResponseEntity.ok().build();
    }
}