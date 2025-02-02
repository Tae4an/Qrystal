package com.qrystal.app.exam.dto;

import com.qrystal.app.exam.domain.ExamAttemptStatus;
import com.qrystal.app.exam.model.Exam;
import com.qrystal.app.exam.model.ExamAttempt;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExamAttemptCreateDto {
    private Long examId;
    
    public ExamAttempt toEntity(Long userId, Exam exam) {
        return ExamAttempt.builder()
                .examId(examId)
                .userId(userId)
                .timeLimit(exam.getTimeLimit())
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusMinutes(exam.getTimeLimit()))
                .status(ExamAttemptStatus.IN_PROGRESS)
                .isTimeExpired(false)
                .build();
    }
}