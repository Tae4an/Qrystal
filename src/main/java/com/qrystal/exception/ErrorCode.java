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
    LOGIN_FAILED(401, "아이디 또는 비밀번호가 올바르지 않습니다"),
    INVALID_PASSWORD(400, "비밀번호가 일치하지 않습니다."),
    PASSWORD_MISMATCH(400, "새 비밀번호가 일치하지 않습니다."),
    VERIFICATION_EXPIRED(400, "인증 시간이 만료되었습니다"),
    ALREADY_VERIFIED(400, "이미 인증이 완료된 계정입니다"),
    EMAIL_SEND_FAILED(500, "이메일 발송에 실패했습니다"),
    EMAIL_NOT_VERIFIED(403, "이메일 인증이 필요합니다"),
    INVALID_VERIFICATION_TOKEN(400, "유효하지 않은 인증 토큰입니다");

    private final int status;
    private final String message;
}