package com.qrystal.app.exam.dto;

import com.qrystal.app.exam.model.ExamQuestion;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
public class ExamQuestionResponse {
    private Long questionId;
    private int questionNumber;
    private int point;
    private String title;
    private String content;
    private String typeName;
    private int difficulty;

    public static ExamQuestionResponse of(ExamQuestion examQuestion) {
        ExamQuestionResponse response = new ExamQuestionResponse();
        response.setQuestionId(examQuestion.getQuestionId());
        response.setQuestionNumber(examQuestion.getQuestionNumber());
        response.setPoint(examQuestion.getPoint());
        response.setTitle(examQuestion.getQuestionTitle());
        response.setContent(examQuestion.getQuestionContent());
        response.setDifficulty(examQuestion.getQuestionDifficulty());
        return response;
    }
}