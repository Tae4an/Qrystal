package com.qrystal.app.category.mapper;

import com.qrystal.app.category.model.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    
    // 전체 조회
    List<Category> findAll();

    // 활성화된 카테고리만 조회
    List<Category> findAllActive();

    // ID로 조회
    Category findById(Long id);

    // 상위 카테고리로 조회
    List<Category> findByParentId(Long parentId);

    // 레벨로 조회
    List<Category> findByLevel(int level);

    // 활성화된 카테고리 중 특정 레벨만 조회
    List<Category> findActiveByLevel(int level);
}