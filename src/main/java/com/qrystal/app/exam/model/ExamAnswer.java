package com.qrystal.app.exam.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamAnswer {
    private Long id;
    private Long attemptId;
    private Long questionId;
    private Long questionTypeId;
    private String submittedAnswer;
    private Boolean isCorrect;
    private Integer score;
    private String gradingComment;
    private Boolean isGraded;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 추가 필드 (조회용)
    private String questionTitle;
    private String correctAnswer;
    private String explanation;
}