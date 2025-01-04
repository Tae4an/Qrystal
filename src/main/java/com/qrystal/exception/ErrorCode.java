package com.qrystal.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_INPUT(400, "Invalid input"),
    UNAUTHORIZED(401, "Unauthorized access"),
    NOT_FOUND(404, "Resource not found"),
    INTERNAL_ERROR(500, "Internal server error"),

    DUPLICATE_USERNAME(400, "이미 사용 중인 사용자명입니다"),
    DUPLICATE_EMAIL(400, "이미 사용 중인 이메일입니다"),
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다"),
    LOGIN_FAILED(401, "아이디 또는 비밀번호가 올바르지 않습니다");

    private final int status;
    private final String message;
}