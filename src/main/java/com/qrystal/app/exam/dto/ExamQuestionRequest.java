package com.qrystal.app.exam.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExamQuestionRequest {
    private Long questionId;
    private int questionNumber;
    private int point;
}
