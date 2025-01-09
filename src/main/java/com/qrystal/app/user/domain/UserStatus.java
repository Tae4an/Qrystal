package com.qrystal.app.user.domain;

public enum UserStatus {
    PENDING,    // 이메일 인증 대기
    ACTIVE,     // 활성화
    INACTIVE    // 비활성화(탈퇴)
}