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
}