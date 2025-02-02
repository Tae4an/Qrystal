package com.qrystal.app.exam.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExamAnswerSubmitDto {
    private Long questionId;
    private String submittedAnswer;
}