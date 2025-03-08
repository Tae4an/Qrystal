package com.qrystal.app.user.dto;

import lombok.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticsResponse {
    private int totalAttempts;           // 총 응시 횟수
    private double averageScore;         // 평균 점수
    private double averageCorrectRate;   // 평균 정답률
    private List<MonthlyStatDto> monthlyData;  // 월별 통계
    private Map<String, Long> correctRateDistribution;  // 정답률 분포

    public static StatisticsResponse empty() {
        return StatisticsResponse.builder()
                .totalAttempts(0)
                .averageScore(0.0)
                .averageCorrectRate(0.0)
                .monthlyData(Collections.emptyList())
                .correctRateDistribution(Collections.emptyMap())
                .build();
    }
}