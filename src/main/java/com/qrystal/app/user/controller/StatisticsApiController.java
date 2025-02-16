package com.qrystal.app.user.controller;

import com.qrystal.app.exam.dto.ExamAttemptResponseDto;
import com.qrystal.app.exam.service.ExamAttemptService;
import com.qrystal.app.user.dto.MonthlyStatDto;
import com.qrystal.app.user.dto.StatisticsResponse;
import com.qrystal.app.user.dto.UserResponse;
import com.qrystal.app.user.service.UserService;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Slf4j
public class StatisticsApiController {
    private final ExamAttemptService examAttemptService;
    private final UserService userService;

    @GetMapping("/overall")
    public StatisticsResponse getOverallStatistics(@AuthenticationPrincipal Object principal) {
        String email = userService.extractEmail(principal);
        UserResponse user = userService.findByEmail(email);

        List<ExamAttemptResponseDto> attempts = examAttemptService.getMyAttempts(user.getId());
        return calculateStatistics(attempts);
    }

    @GetMapping("/exam/{examId}")
    public StatisticsResponse getExamStatistics(@PathVariable Long examId,
                                              @AuthenticationPrincipal Object principal) {
        String email = userService.extractEmail(principal);
        UserResponse user = userService.findByEmail(email);

        List<ExamAttemptResponseDto> attempts = examAttemptService.getMyAttempts(user.getId())
            .stream()
            .filter(attempt -> attempt.getExamId().equals(examId))
            .collect(Collectors.toList());

        return calculateStatistics(attempts);
    }

    private StatisticsResponse calculateStatistics(List<ExamAttemptResponseDto> attempts) {
        if (attempts.isEmpty()) {
            return StatisticsResponse.empty();
        }

        // 기본 통계 계산
        double averageScore = attempts.stream()
                .mapToInt(ExamAttemptResponseDto::getTotalScore)
                .average()
                .orElse(0.0);

        double averageCorrectRate = attempts.stream()
                .mapToDouble(ExamAttemptResponseDto::getCorrectRate)
                .average()
                .orElse(0.0);

        // 월별 통계
        Map<String, List<ExamAttemptResponseDto>> monthlyGroups = attempts.stream()
                .collect(Collectors.groupingBy(a ->
                        a.getSubmittedAt().format(DateTimeFormatter.ofPattern("yyyy-MM"))));

        List<MonthlyStatDto> monthlyData = monthlyGroups.entrySet().stream()
                .map(entry -> {
                    List<ExamAttemptResponseDto> monthAttempts = entry.getValue();
                    return new MonthlyStatDto(
                            entry.getKey(),
                            monthAttempts.size(),
                            monthAttempts.stream()
                                    .mapToDouble(ExamAttemptResponseDto::getCorrectRate)
                                    .average()
                                    .orElse(0.0)
                    );
                })
                .sorted(Comparator.comparing(MonthlyStatDto::getMonth))
                .collect(Collectors.toList());

        // 정답률 분포
        Map<String, Long> correctRateGroups = attempts.stream()
                .collect(Collectors.groupingBy(
                        attempt -> {
                            double rate = attempt.getCorrectRate();
                            if (rate >= 90) return "90% 이상";
                            if (rate >= 70) return "70-90%";
                            if (rate >= 50) return "50-70%";
                            return "50% 미만";
                        },
                        Collectors.counting()
                ));

        return StatisticsResponse.builder()
                .totalAttempts(attempts.size())
                .averageScore(averageScore)
                .averageCorrectRate(averageCorrectRate)
                .monthlyData(monthlyData)
                .correctRateDistribution(correctRateGroups)
                .build();
    }
}