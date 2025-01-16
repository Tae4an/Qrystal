package com.qrystal.app.admin.service;

import com.qrystal.app.admin.domain.AdminRole;
import com.qrystal.app.admin.domain.AdminStatus;
import com.qrystal.app.admin.dto.AdminCreateRequest;
import com.qrystal.app.admin.dto.AdminResponse;
import com.qrystal.app.admin.dto.AdminUpdateRequest;
import com.qrystal.app.admin.mapper.AdminMapper;
import com.qrystal.app.admin.model.Admin;
import com.qrystal.exception.CustomException;
import com.qrystal.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {
    private final AdminMapper adminMapper;
    private final PasswordEncoder passwordEncoder;

    public List<AdminResponse> getAllAdmins() {
        return adminMapper.findAll().stream()
                .map(AdminResponse::of)
                .collect(Collectors.toList());
    }
    public AdminResponse getAdmin(Long id) {
        Admin admin = adminMapper.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ADMIN_NOT_FOUND));
        return AdminResponse.of(admin);
    }
    @Transactional
    public void createAdmin(AdminCreateRequest request) {
        // 관리자 ID 중복 검사
        if (adminMapper.findByAdminId(request.getAdminId()).isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_ADMIN_ID);
        }

        Admin admin = Admin.builder()
                .adminId(request.getAdminId())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .role(request.getRole())
                .status(AdminStatus.ACTIVE)
                .build();

        adminMapper.save(admin);
    }

    @Transactional
    public void updateAdmin(Long id, AdminUpdateRequest request) {
        Admin admin = adminMapper.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ADMIN_NOT_FOUND));

        Admin updatedAdmin = Admin.builder()
                .id(id)
                .adminId(admin.getAdminId())
                .password(request.getPassword() != null ?
                        passwordEncoder.encode(request.getPassword()) : admin.getPassword())
                .name(request.getName())
                .role(request.getRole())
                .status(admin.getStatus())
                .lastLoginAt(admin.getLastLoginAt())
                .createdAt(admin.getCreatedAt())
                .updatedAt(admin.getUpdatedAt())
                .build();

        adminMapper.update(updatedAdmin);
    }

    @Transactional
    public void updateAdminStatus(Long id, AdminStatus status) {
        Admin admin = adminMapper.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ADMIN_NOT_FOUND));

        // SUPER_ADMIN은 비활성화할 수 없음
        if (admin.getRole() == AdminRole.SUPER_ADMIN && status == AdminStatus.INACTIVE) {
            throw new CustomException(ErrorCode.SUPER_ADMIN_DELETION);
        }

        Admin updatedAdmin = Admin.builder()
                .id(id)
                .adminId(admin.getAdminId())
                .password(admin.getPassword())
                .name(admin.getName())
                .role(admin.getRole())
                .status(status)
                .lastLoginAt(admin.getLastLoginAt())
                .createdAt(admin.getCreatedAt())
                .updatedAt(admin.getUpdatedAt())
                .build();

        adminMapper.update(updatedAdmin);
    }

    @Transactional
    public void updateLastLogin(Long id) {
        Admin admin = adminMapper.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.ADMIN_NOT_FOUND));

        adminMapper.updateLastLogin(id, LocalDateTime.now());
    }
}