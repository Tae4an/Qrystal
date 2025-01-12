package com.qrystal.app.admin.model;

import com.qrystal.app.admin.domain.AdminRole;
import com.qrystal.app.admin.domain.AdminStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Admin {
    private Long id;
    private String adminId;          // 관리자 아이디
    private String password;         // 암호화된 비밀번호
    private String name;             // 관리자 이름
    private AdminRole role;          // 권한
    private AdminStatus status;      // 상태
    private LocalDateTime lastLoginAt;    // 마지막 로그인 시간
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}