package com.qrystal.app.question.dto;

import com.qrystal.app.question.domain.QuestionStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class QuestionSearchCondition {
    private Long categoryId;
    private Long userId;
    private Long typeId;
    private QuestionStatus status;
    private Boolean isPublic;
}