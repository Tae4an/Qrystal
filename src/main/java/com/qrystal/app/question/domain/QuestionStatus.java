package com.qrystal.app.question.domain;

import lombok.Getter;

@Getter
public enum QuestionStatus {
    ACTIVE("활성"),
    INACTIVE("비활성"),
    REPORTED("신고됨");

    private final String value;

    QuestionStatus(String value) {
        this.value = value;
    }
}