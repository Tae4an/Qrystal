package com.qrystal.app.exam.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.qrystal.app.exam.domain.ExamStatus;
import com.qrystal.app.exam.model.Exam;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Getter @Setter
public class ExamCreateRequest {
    private String title;
    private String description;
    private int timeLimit;
    private Long categoryId;

    @JsonProperty("isPublic")
    private boolean isPublic;

    private ExamStatus status;
    private List<ExamQuestionRequest> questions;

    public Exam toEntity() {
        Exam exam = new Exam();
        exam.setTitle(title);
        exam.setDescription(description);
        exam.setTimeLimit(timeLimit);
        exam.setCategoryId(categoryId);
        exam.setPublic(isPublic);
        exam.setStatus(status);
        exam.setTotalPoints(questions.stream()
                .mapToInt(ExamQuestionRequest::getPoint)
                .sum());
        return exam;
    }
}