package com.qrystal.app.exam.domain;

public enum ExamStatus {
    DRAFT("임시저장"),
    PUBLISHED("공개"),
    CLOSED("마감");

    private final String value;

    ExamStatus(String value) {
        this.value = value;
    }
}