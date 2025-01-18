package com.qrystal.app.question.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class QuestionChoiceRequest {
    @NotNull(message = "보기 번호는 필수입니다")
    private Integer choiceNumber;

    @NotBlank(message = "보기 내용은 필수입니다")
    private String content;
}