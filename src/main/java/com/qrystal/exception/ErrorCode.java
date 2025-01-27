package com.qrystal.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // General
    INVALID_INPUT(400, "Invalid input"),
    UNAUTHORIZED(401, "Unauthorized access"),
    NOT_FOUND(404, "Resource not found"),
    INTERNAL_ERROR(500, "Internal server error"),

    // User
    DUPLICATE_USERNAME(400, "Username is already in use"),
    DUPLICATE_EMAIL(400, "Email is already in use"),
    USER_NOT_FOUND(404, "User not found"),
    LOGIN_FAILED(401, "Incorrect username or password"),
    INVALID_PASSWORD(400, "Password does not match"),
    PASSWORD_MISMATCH(400, "New passwords do not match"),

    // Verification
    VERIFICATION_EXPIRED(400, "Verification time has expired"),
    ALREADY_VERIFIED(400, "Account is already verified"),
    EMAIL_SEND_FAILED(500, "Failed to send email"),
    EMAIL_NOT_VERIFIED(403, "Email verification required"),
    INVALID_VERIFICATION_TOKEN(400, "Invalid verification token"),

    // Question
    QUESTION_NOT_FOUND(404, "Question not found"),
    INVALID_QUESTION_TYPE(400, "Invalid question type"),
    INVALID_QUESTION_STATUS(400, "Invalid question status"),

    // Exam
    EXAM_NOT_FOUND(404, "Exam not found"),
    EXAM_ACCESS_DENIED(403, "Access to exam denied"),
    EXAM_STATUS_INVALID(400, "Invalid exam status"),
    EXAM_ALREADY_EXISTS(400, "Exam already exists"),
    EXAM_QUESTIONS_REQUIRED(400, "Exam must have at least one question");

    private final int status;
    private final String message;
}