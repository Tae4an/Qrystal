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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "통계", description = "사용자 통계 관련 API")
public class StatisticsApiController {
    private final ExamAttemptService examAttemptService;
    private final UserService userService;

    @GetMapping("/overall")
    @Operation(summary = "전체 통계 조회", description = "사용자의 전체 시험 통계를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 통계를 반환함",
            content = @Content(schema = @Schema(implementation = StatisticsResponse.class)))
    public StatisticsResponse getOverallStatistics(@Parameter(hidden = true) @AuthenticationPrincipal Object principal) {
        String email = userService.extractEmail(principal);
        UserResponse user = userService.findByEmail(email);

        List<ExamAttemptResponseDto> attempts = examAttemptService.getMyAttempts(user.getId());
        return calculateStatistics(attempts);
    }

    @GetMapping("/exam/{examId}")
    @Operation(summary = "특정 시험 통계 조회", description = "사용자의 특정 시험에 대한 통계를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 통계를 반환함",
            content = @Content(schema = @Schema(implementation = StatisticsResponse.class)))
    public StatisticsResponse getExamStatistics(
            @Parameter(description = "조회할 시험 ID") @PathVariable Long examId,
            @Parameter(hidden = true) @AuthenticationPrincipal Object principal) {
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