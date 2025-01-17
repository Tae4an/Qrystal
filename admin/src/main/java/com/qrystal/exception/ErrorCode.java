package com.qrystal.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // Common
    NOT_FOUND(404, "리소스를 찾을 수 없습니다"),
    INTERNAL_ERROR(500, "서버 오류가 발생했습니다"),
    
    // Admin
    ADMIN_NOT_FOUND(404, "관리자를 찾을 수 없습니다"),
    DUPLICATE_ADMIN_ID(400, "이미 사용중인 관리자 ID입니다"),
    INVALID_ADMIN_STATUS(400, "잘못된 관리자 상태 변경입니다"),
    SUPER_ADMIN_DELETION(400, "최고 관리자는 비활성화할 수 없습니다"),

    // User
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다."),
    INVALID_USER_STATUS(400, "잘못된 사용자 상태입니다.");
    private final int status;
    private final String message;
}