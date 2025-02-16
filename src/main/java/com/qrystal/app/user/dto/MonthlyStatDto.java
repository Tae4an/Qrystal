package com.qrystal.app.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyStatDto {
    private String month;              // 월 (YYYY-MM 형식)
    private int totalAttempts;         // 해당 월의 총 응시 횟수
    private double averageCorrectRate; // 해당 월의 평균 정답률
}