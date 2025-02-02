package com.qrystal.app.exam.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ExamAttemptStatus {
    READY("준비"),         // 시험 시작 전 상태
    IN_PROGRESS("진행중"),  // 시험 진행 중
    SUBMITTED("제출완료"),  // 답안 제출 완료
    GRADED("채점완료"),    // 채점 완료
    TIMEOUT("시간초과");    // 시험 시간 초과

    private final String value;
}