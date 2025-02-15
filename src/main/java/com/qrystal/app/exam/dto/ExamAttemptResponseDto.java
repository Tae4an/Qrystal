package com.qrystal.app.exam.dto;

import com.qrystal.app.exam.domain.ExamAttemptStatus;
import com.qrystal.app.exam.model.Exam;
import com.qrystal.app.exam.model.ExamAttempt;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
public class ExamAttemptResponseDto {
    private Long id;
    private Long examId;
    private String examTitle;        // 시험 제목
    private String categoryName;     // 카테고리명
    private Integer totalPoints;     // 시험 총점
    private Boolean isPublic;        // 시험 공개 여부
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime submittedAt;
    private Integer timeLimit;
    private Integer totalScore;
    private ExamAttemptStatus status;
    private Boolean isTimeExpired;
    private List<ExamAnswerResponseDto> answers;
    private Double correctRate;
    private Integer correctCount;
    private Integer wrongCount;
    private Long userId;

    public static ExamAttemptResponseDto from(ExamAttempt attempt) {
        ExamAttemptResponseDto dto = new ExamAttemptResponseDto();
        BeanUtils.copyProperties(attempt, dto);

        dto.setUserId(attempt.getUserId());

        if (attempt.getAnswers() != null) {
            dto.setAnswers(attempt.getAnswers().stream()
                    .map(ExamAnswerResponseDto::from)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
    public static ExamAttemptResponseDto from(ExamAttempt attempt, Exam exam) {
        ExamAttemptResponseDto dto = from(attempt);  // 기존 메서드 재사용

        if (exam != null) {
            dto.setExamTitle(exam.getTitle());
            dto.setCategoryName(exam.getCategoryName());
            dto.setTotalPoints(exam.getTotalPoints());
            dto.setIsPublic(exam.isPublic());
        }

        return dto;
    }
}