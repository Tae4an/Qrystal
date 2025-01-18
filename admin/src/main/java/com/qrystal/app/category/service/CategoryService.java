package com.qrystal.app.category.service;

import com.qrystal.app.category.domain.CategoryStatus;
import com.qrystal.app.category.dto.CategoryCreateRequest;
import com.qrystal.app.category.dto.CategoryUpdateRequest;
import com.qrystal.app.category.mapper.CategoryMapper;
import com.qrystal.app.category.model.Category;
import com.qrystal.exception.CustomException;
import com.qrystal.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryMapper categoryMapper;
    private static final int MAX_DEPTH = 3;

    public List<Category> getAllCategories() {
        List<Category> allCategories = categoryMapper.findAll();
        return buildHierarchy(allCategories);
    }
    public Category findById(Long id) {
        Category category = categoryMapper.findById(id);
        if (category == null) {
            throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        return category;
    }

    // 카테고리 생성
    @Transactional
    public Category createCategory(CategoryCreateRequest request) {
        Category category = Category.builder()
                .name(request.getName())
                .parentId(request.getParentId())
                .description(request.getDescription())
                .status(CategoryStatus.fromString(request.getStatus()))
                .build();

        if (category.getParentId() != null) {
            Category parent = categoryMapper.findById(category.getParentId());
            if (parent == null) {
                throw new CustomException(ErrorCode.PARENT_CATEGORY_NOT_FOUND);
            }

            // 부모 카테고리 상태 검증
            if (parent.getStatus() == CategoryStatus.INACTIVE) {
                throw new CustomException(ErrorCode.INACTIVE_PARENT_CATEGORY);
            }

            // 최대 깊이 검증
            if (parent.getLevel() >= MAX_DEPTH) {
                throw new CustomException(ErrorCode.MAX_DEPTH_EXCEEDED);
            }

            category.setLevel(parent.getLevel() + 1);
        } else {
            category.setLevel(1);
        }

        // 이름 중복 검증
        validateDuplicateName(category);

        // ordering 설정
        int maxOrdering = categoryMapper.findMaxOrderingByParentId(category.getParentId());
        category.setOrdering(maxOrdering + 1);

        categoryMapper.save(category);
        return category;
    }

    // 카테고리 수정
    @Transactional
    public void updateCategory(Long id, CategoryUpdateRequest request) {
        Category category = categoryMapper.findById(id);
        if (category == null) {
            throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setStatus(CategoryStatus.valueOf(request.getStatus()));

        categoryMapper.update(category);
    }

    // 카테고리 삭제
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryMapper.findById(id);
        if (category == null) {
            throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        // 하위 카테고리 존재 여부 검증
        List<Category> children = categoryMapper.findByParentId(id);
        if (!children.isEmpty()) {
            throw new CustomException(ErrorCode.CATEGORY_HAS_CHILDREN);
        }

        categoryMapper.delete(id);
    }

    // 카테고리 순서 변경
    @Transactional
    public void updateCategoryOrder(Long id, int newOrder) {
        Category category = categoryMapper.findById(id);
        if (category == null) {
            throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        List<Category> siblings = categoryMapper.findSiblingsById(id);

        // 유효한 순서값인지 검증
        if (newOrder < 0 || newOrder >= siblings.size()) {
            throw new CustomException(ErrorCode.INVALID_CATEGORY_ORDER);
        }

        reorderCategories(category, siblings, newOrder);
    }

    private void validateDuplicateName(Category category) {
        List<Category> siblings = categoryMapper.findByParentId(category.getParentId());
        boolean isDuplicate = siblings.stream()
                .filter(sibling -> !sibling.getId().equals(category.getId()))
                .anyMatch(sibling -> sibling.getName().equals(category.getName()));

        if (isDuplicate) {
            throw new CustomException(ErrorCode.DUPLICATE_CATEGORY_NAME);
        }
    }

    private void reorderCategories(Category category, List<Category> siblings, int newOrder) {
        int oldOrder = category.getOrdering();

        // 순서가 변경된 경우만 처리
        if (oldOrder != newOrder) {
            // 이동 방향에 따라 다른 카테고리들의 순서 조정
            if (newOrder > oldOrder) {
                // 위로 이동: oldOrder와 newOrder 사이의 항목들을 한 칸씩 위로
                siblings.stream()
                        .filter(s -> s.getOrdering() <= newOrder && s.getOrdering() > oldOrder)
                        .forEach(s -> categoryMapper.updateOrdering(s.getId(), s.getOrdering() - 1));
            } else {
                // 아래로 이동: newOrder와 oldOrder 사이의 항목들을 한 칸씩 아래로
                siblings.stream()
                        .filter(s -> s.getOrdering() >= newOrder && s.getOrdering() < oldOrder)
                        .forEach(s -> categoryMapper.updateOrdering(s.getId(), s.getOrdering() + 1));
            }

            // 대상 카테고리의 새로운 순서 설정
            categoryMapper.updateOrdering(category.getId(), newOrder);
        }
    }

    // 계층 구조 생성 헬퍼 메소드
    private List<Category> buildHierarchy(List<Category> categories) {
        Map<Long, Category> categoryMap = new HashMap<>();
        List<Category> rootCategories = new ArrayList<>();

        // 모든 카테고리를 맵에 저장
        for (Category category : categories) {
            categoryMap.put(category.getId(), category);
            category.setChildren(new ArrayList<>());
        }

        // 계층 구조 구성
        for (Category category : categories) {
            if (category.getParentId() == null) {
                rootCategories.add(category);
            } else {
                Category parent = categoryMap.get(category.getParentId());
                if (parent != null) {
                    parent.getChildren().add(category);
                }
            }
        }

        return rootCategories;
    }
}