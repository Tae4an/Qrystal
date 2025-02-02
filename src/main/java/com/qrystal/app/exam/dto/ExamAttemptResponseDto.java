package com.qrystal.app.exam.dto;

import com.qrystal.app.exam.domain.ExamAttemptStatus;
import com.qrystal.app.exam.model.ExamAttempt;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExamAttemptResponseDto {
    private Long id;
    private Long examId;
    private String examTitle;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime submittedAt;
    private Integer timeLimit;
    private Integer totalScore;
    private ExamAttemptStatus status;
    private Boolean isTimeExpired;
    private List<ExamAnswerResponseDto> answers;
    
    public static ExamAttemptResponseDto from(ExamAttempt attempt) {
        ExamAttemptResponseDto dto = new ExamAttemptResponseDto();
        BeanUtils.copyProperties(attempt, dto);
        
        if (attempt.getAnswers() != null) {
            dto.setAnswers(attempt.getAnswers().stream()
                    .map(ExamAnswerResponseDto::from)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
}