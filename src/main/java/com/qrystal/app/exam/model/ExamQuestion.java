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
    private int questionNumber;
    private int point;
    private LocalDateTime createdAt;
    private String questionTitle;
    private String questionContent;
    private Long questionTypeId;
    private int questionDifficulty;
}