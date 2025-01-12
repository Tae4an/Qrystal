package com.qrystal.app.admin.mapper;

import com.qrystal.app.admin.model.Admin;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface AdminMapper {
    void save(Admin admin);
    Optional<Admin> findById(Long id);
    Optional<Admin> findByAdminId(String adminId);
    void updateLastLogin(Long id, LocalDateTime lastLoginAt);
    void update(Admin admin);
    List<Admin> findAll();
}