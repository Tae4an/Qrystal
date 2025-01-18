package com.qrystal.app.question.model;

import com.qrystal.app.question.domain.QuestionStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    private Long id;
    private Long categoryId;
    private Long userId;
    private Long typeId;
    private String title;
    private String content;
    private String answer;
    private String explanation;
    private Integer difficulty;
    private Boolean isPublic;
    private QuestionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 추가 필드
    private String categoryName;    // 카테고리명
    private String userName;        // 작성자명
    private String typeName;        // 문제 유형명
    private List<QuestionChoice> choices;  // 객관식 보기
    private List<String> tags;      // 태그 목록
}