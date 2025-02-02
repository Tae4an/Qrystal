package com.qrystal.app.exam.dto;

import com.qrystal.app.exam.model.ExamAnswer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExamAnswerResponseDto {
    private Long id;
    private Long questionId;
    private String questionTitle;
    private String submittedAnswer;
    private Boolean isCorrect;
    private Integer score;
    private String gradingComment;
    private Boolean isGraded;
    private String correctAnswer;
    private String explanation;
    
    public static ExamAnswerResponseDto from(ExamAnswer answer) {
        ExamAnswerResponseDto dto = new ExamAnswerResponseDto();
        BeanUtils.copyProperties(answer, dto);
        return dto;
    }
}