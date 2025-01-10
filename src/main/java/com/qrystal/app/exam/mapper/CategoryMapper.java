package com.qrystal.app.exam.mapper;

import com.qrystal.app.exam.model.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CategoryMapper {
    void save(Category category);
    Optional<Category> findById(Long id);
    List<Category> findAll();
    List<Category> findByParentId(Long parentId);
    void update(Category category);
    void delete(Long id);
}