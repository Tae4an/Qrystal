package com.qrystal.app.exam.model;

import com.qrystal.app.question.model.QuestionChoice;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamAnswer {
    private Long id;
    private Long attemptId;
    private Long questionId;
    private Integer questionNumber;
    private Long questionTypeId;
    private String submittedAnswer;
    private Boolean isCorrect;
    private Integer score;
    private Integer maxScore;
    private String gradingComment;
    private Boolean isGraded;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 추가 필드 (조회용)
    private String questionTitle;
    private String correctAnswer;
    private String explanation;
    private List<QuestionChoice> choices;
}