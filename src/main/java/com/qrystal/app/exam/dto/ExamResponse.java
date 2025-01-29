package com.qrystal.app.exam.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.qrystal.app.exam.domain.ExamStatus;
import com.qrystal.app.exam.model.Exam;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ExamResponse {
    private Long id;
    private String title;
    private String description;
    private int timeLimit;
    private int totalPoints;
    private Long categoryId;
    private String categoryName;
    private String createdByName;
    private ExamStatus status;
    @JsonProperty("isPublic")
    private boolean isPublic;
    private LocalDateTime createdAt;
    private List<ExamQuestionResponse> questions;

    public static ExamResponse of(Exam exam) {
        ExamResponse response = new ExamResponse();
        BeanUtils.copyProperties(exam, response);
        response.setCategoryId(exam.getCategoryId());  
        response.setQuestions(exam.getQuestions().stream()
                .map(ExamQuestionResponse::of)
                .collect(Collectors.toList()));
        return response;
    }
}