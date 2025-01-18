package com.qrystal.app.question.dto;

import com.qrystal.app.question.model.QuestionChoice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionChoiceResponse {
    private Long id;
    private Integer choiceNumber;
    private String content;

    public static QuestionChoiceResponse from(QuestionChoice choice) {
        return QuestionChoiceResponse.builder()
                .id(choice.getId())
                .choiceNumber(choice.getChoiceNumber())
                .content(choice.getContent())
                .build();
    }
}