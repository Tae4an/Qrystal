package com.qrystal.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_INPUT(400, "Invalid input"),
    UNAUTHORIZED(401, "Unauthorized access"),
    NOT_FOUND(404, "Resource not found"),
    INTERNAL_ERROR(500, "Internal server error");

    private final int status;
    private final String message;
}