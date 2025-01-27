package com.qrystal.app.exam.dto;

import com.qrystal.app.exam.model.Exam;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExamCreateRequest {
    private String title;
    private String description;
    private int timeLimit;
    private Long categoryId;
    private boolean isPublic;
    private List<ExamQuestionRequest> questions;
    
    public Exam toEntity() {
        Exam exam = new Exam();
        exam.setTitle(title);
        exam.setDescription(description);
        exam.setTimeLimit(timeLimit);
        exam.setCategoryId(categoryId);
        exam.setPublic(isPublic);
        // 총점은 문제 배점의 합으로 계산
        exam.setTotalPoints(questions.stream()
                .mapToInt(ExamQuestionRequest::getPoint)
                .sum());
        return exam;
    }
}