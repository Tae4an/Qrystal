package com.qrystal.app.admin.dto;

import com.qrystal.app.admin.domain.AdminRole;
import com.qrystal.app.admin.domain.AdminStatus;
import com.qrystal.app.admin.model.Admin;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class AdminResponse {
    private Long id;
    private String adminId;
    private String name;
    private AdminRole role;
    private AdminStatus status;
    private String lastLoginAt;
    private String createdAt;

    public static AdminResponse of(Admin admin) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return AdminResponse.builder()
                .id(admin.getId())
                .adminId(admin.getAdminId())
                .name(admin.getName())
                .role(admin.getRole())
                .status(admin.getStatus())
                .lastLoginAt(admin.getLastLoginAt() != null ?
                        admin.getLastLoginAt().format(formatter) : null)
                .createdAt(admin.getCreatedAt() != null ?
                        admin.getCreatedAt().format(formatter) : null)
                .build();
    }
}