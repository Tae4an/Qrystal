package com.qrystal.app.admin.dto;

import com.qrystal.app.admin.domain.AdminRole;
import com.qrystal.app.admin.domain.AdminStatus;
import com.qrystal.app.admin.model.Admin;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AdminResponse {
    private Long id;
    private String adminId;
    private String name;
    private AdminRole role;
    private AdminStatus status;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    
    public static AdminResponse of(Admin admin) {
        return AdminResponse.builder()
                .id(admin.getId())
                .adminId(admin.getAdminId())
                .name(admin.getName())
                .role(admin.getRole())
                .status(admin.getStatus())
                .lastLoginAt(admin.getLastLoginAt())
                .createdAt(admin.getCreatedAt())
                .build();
    }
}