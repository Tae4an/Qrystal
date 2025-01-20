package com.qrystal.app.category.domain;

import lombok.Getter;

@Getter
public enum CategoryStatus {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE");

    private final String value;

    CategoryStatus(String value) {
        this.value = value;
    }

    // String을 Enum으로 안전하게 변환하는 메서드
    public static CategoryStatus fromString(String value) {
        try {
            return CategoryStatus.valueOf(value);
        } catch (IllegalArgumentException e) {
            return INACTIVE;  // 기본값 설정
        }
    }
}