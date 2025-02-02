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
    EXAM_NOT_PUBLISHED(400, "시험이 공개되지 않았습니다"),
    EXAM_TIMER_ERROR(500, "시험 타이머 처리 중 오류가 발생했습니다"),
    EXAM_ALREADY_IN_PROGRESS(400, "이미 진행 중인 시험이 있습니다"),
    EXAM_NOT_IN_PROGRESS(400, "진행 중인 시험이 없습니다"),
    EXAM_TIME_EXPIRED(400, "시험 시간이 만료되었습니다"),
    EXAM_NOT_STARTED(400, "시험이 시작되지 않았습니다"),
    EXAM_ALREADY_SUBMITTED(400, "이미 제출된 시험입니다"),
    INVALID_SUBMISSION(400, "잘못된 제출입니다"),
    GRADING_ERROR(500, "채점 중 오류가 발생했습니다");
    private final int status;
    private final String message;
}