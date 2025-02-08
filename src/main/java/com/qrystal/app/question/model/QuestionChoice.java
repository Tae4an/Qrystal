package com.qrystal.app.question.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionChoice {
    private Long id;
    private Long questionId;
    private Integer choiceNumber;
    private String content;
    private LocalDateTime createdAt;

    // 추가 필드들 - 객관식 문제 채점용
    private Boolean isCorrect;    // 정답 여부
    private Boolean isSelected;   // 사용자가 선택했는지 여부
}