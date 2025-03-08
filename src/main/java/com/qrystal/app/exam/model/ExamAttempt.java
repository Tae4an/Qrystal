package com.qrystal.app.exam.model;

import com.qrystal.app.exam.domain.ExamAttemptStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamAttempt {
    private Long id;
    private Long examId;
    private Long userId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime submittedAt;
    private Integer timeLimit;
    private Integer totalScore;
    private ExamAttemptStatus status;
    private Boolean isTimeExpired;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 추가 필드 (조회용)
    private String examTitle;
    private String userName;
    private List<ExamAnswer> answers;
    private String categoryName;
    private Integer totalPoints;
    private Boolean isPublic;
}