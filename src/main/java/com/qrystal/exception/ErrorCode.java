package com.qrystal.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // General
    INVALID_INPUT(400, "잘못된 입력입니다."),
    UNAUTHORIZED(401, "인증되지 않은 접근입니다."),
    NOT_FOUND(404, "리소스를 찾을 수 없습니다."),
    INTERNAL_ERROR(500, "내부 서버 오류가 발생했습니다."),
    RESOURCE_ACCESS_DENIED(403, "리소스에 대한 접근 권한이 없습니다."),

    // User
    DUPLICATE_USERNAME(400, "이미 사용 중인 사용자 이름입니다."),
    DUPLICATE_EMAIL(400, "이미 사용 중인 이메일입니다."),
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다."),
    LOGIN_FAILED(401, "잘못된 사용자 이름 또는 비밀번호입니다."),
    INVALID_PASSWORD(400, "비밀번호가 일치하지 않습니다."),
    PASSWORD_MISMATCH(400, "새 비밀번호가 일치하지 않습니다."),

    // Verification
    VERIFICATION_EXPIRED(400, "인증 시간이 만료되었습니다."),
    ALREADY_VERIFIED(400, "이미 인증된 계정입니다."),
    EMAIL_SEND_FAILED(500, "이메일 전송에 실패했습니다."),
    EMAIL_NOT_VERIFIED(403, "이메일 인증이 필요합니다."),
    INVALID_VERIFICATION_TOKEN(400, "유효하지 않은 인증 토큰입니다."),

    // Question
    QUESTION_NOT_FOUND(404, "문제를 찾을 수 없습니다."),
    INVALID_QUESTION_TYPE(400, "잘못된 문제 유형입니다."),
    INVALID_QUESTION_STATUS(400, "잘못된 문제 상태입니다."),
    QUESTION_NOT_AVAILABLE(400, "사용할 수 없는 문제입니다."),

    // Exam
    EXAM_NOT_FOUND(404, "시험을 찾을 수 없습니다."),
    EXAM_ACCESS_DENIED(403, "시험에 대한 접근이 거부되었습니다."),
    EXAM_STATUS_INVALID(400, "잘못된 시험 상태입니다."),
    EXAM_ALREADY_EXISTS(400, "이미 존재하는 시험입니다."),
    EXAM_NOT_AVAILABLE(400, "시험이 공개되지 않았습니다."),
    EXAM_TIMER_ERROR(500, "시험 타이머 처리 중 오류가 발생했습니다."),
    EXAM_ALREADY_IN_PROGRESS(400, "이미 진행 중인 시험이 있습니다."),
    EXAM_NOT_IN_PROGRESS(400, "진행 중인 시험이 없습니다."),
    EXAM_TIME_EXPIRED(400, "시험 시간이 만료되었습니다."),
    EXAM_NOT_STARTED(400, "시험이 시작되지 않았습니다."),
    EXAM_ALREADY_SUBMITTED(400, "이미 제출된 시험입니다."),
    INVALID_SUBMISSION(400, "잘못된 제출입니다."),
    GRADING_ERROR(500, "채점 중 오류가 발생했습니다."),
    EXAM_NOT_SUBMITTED(400, "아직 제출되지 않은 시험입니다."),
    EXAM_ATTEMPT_NOT_FOUND(404, "시험 응시 정보를 찾을 수 없습니다."),
    EXAM_DELETE_FAILED(500, "모의고사 삭제 중 오류가 발생했습니다.");

    private final int status;
    private final String message;
}
