package com.qrystal.app.category.service;

import com.qrystal.app.category.mapper.CategoryMapper;
import com.qrystal.app.category.model.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryMapper categoryMapper;

    // 활성화된 카테고리 조회
    public List<Category> getActiveCategories() {
        return categoryMapper.findAllActive();
    }
}