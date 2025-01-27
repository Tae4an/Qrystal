package com.qrystal.app.exam.model;

import com.qrystal.app.question.model.Question;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ExamQuestion {
    private Long examId;
    private Long questionId;
    private int questionNumber;    // 문제 번호
    private int point;            // 배점
    private LocalDateTime createdAt;
    
    // 문제 상세 정보
    private Question question;    // 기존 Question 모델 활용
}