package com.qrystal.app.question.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class QuestionType {
    private Long id;
    private String name;    // 객관식, 주관식, 서술형 등
    private LocalDateTime createdAt;

    // 편의를 위한 정적 팩토리 메서드
    public static QuestionType of(Long id, String name) {
        return new QuestionType(id, name, LocalDateTime.now());
    }
}