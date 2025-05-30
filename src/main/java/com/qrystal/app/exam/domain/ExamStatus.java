package com.qrystal.app.exam.domain;

import lombok.Getter;

@Getter
public enum ExamStatus {
    ACTIVE("활성화"),
    INACTIVE("비활성화");

    private final String value;

    ExamStatus(String value) {
        this.value = value;
    }
}