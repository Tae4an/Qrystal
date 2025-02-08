package com.qrystal.app.exam.dto;

import com.qrystal.app.exam.model.ExamAnswer;
import com.qrystal.app.question.model.QuestionChoice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExamAnswerResponseDto {
    private Long id;
    private Long questionId;
    private Integer questionNumber;
    private Long questionTypeId;
    private String questionTitle;
    private String submittedAnswer;
    private Boolean isCorrect;
    private Integer score;
    private Integer maxScore;
    private String gradingComment;
    private Boolean isGraded;
    private String correctAnswer;
    private String explanation;
    private List<QuestionChoice> choices;

    public static ExamAnswerResponseDto from(ExamAnswer answer) {
        ExamAnswerResponseDto dto = new ExamAnswerResponseDto();
        BeanUtils.copyProperties(answer, dto);
        return dto;
    }
}