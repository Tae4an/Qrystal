package com.qrystal.app.exam.dto;

import com.qrystal.app.exam.model.ExamQuestion;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Getter @Setter
public class ExamQuestionResponse {
    private Long questionId;
    private int questionNumber;
    private int point;
    private String title;
    private String content;
    private String typeName;    // QuestionType 객체 대신 타입 이름만 사용
    private int difficulty;

    public static ExamQuestionResponse of(ExamQuestion examQuestion) {
        ExamQuestionResponse response = new ExamQuestionResponse();
        BeanUtils.copyProperties(examQuestion, response);
        if (examQuestion.getQuestion() != null) {
            response.setTypeName(examQuestion.getQuestion().getTypeName());
        }
        return response;
    }
}