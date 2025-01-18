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
    INVALID_USER_STATUS(400, "잘못된 사용자 상태입니다."),

    // Category
    CATEGORY_NOT_FOUND(404, "카테고리를 찾을 수 없습니다"),
    DUPLICATE_CATEGORY_NAME(400, "같은 부모 카테고리 내에 동일한 이름이 존재합니다"),
    MAX_DEPTH_EXCEEDED(400, "최대 카테고리 깊이를 초과했습니다"),
    INVALID_CATEGORY_ORDER(400, "유효하지 않은 카테고리 순서입니다"),
    CATEGORY_HAS_CHILDREN(400, "하위 카테고리가 존재하는 카테고리는 삭제할 수 없습니다"),
    PARENT_CATEGORY_NOT_FOUND(404, "상위 카테고리를 찾을 수 없습니다"),
    INACTIVE_PARENT_CATEGORY(400, "비활성화된 상위 카테고리 아래에는 카테고리를 생성할 수 없습니다"),
    CATEGORY_STATUS_CHANGE_ERROR(400, "상위 카테고리가 비활성화 상태일 때는 활성화할 수 없습니다");

    private final int status;
    private final String message;
}