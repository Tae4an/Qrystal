package com.qrystal.app.category.mapper;

import com.qrystal.app.category.model.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    // 기본 CRUD
    List<Category> findAll();
    Category findById(Long id);
    void save(Category category);
    void update(Category category);
    void delete(Long id);
    
    // 계층구조 관련
    List<Category> findByParentId(Long parentId);
    List<Category> findByLevel(int level);
    Category findParentById(Long id);
    
    // 순서 관련
    Integer findMaxOrderingByParentId(Long parentId);
    void updateOrdering(Long id, int ordering);
    List<Category> findSiblingsById(Long id);
}