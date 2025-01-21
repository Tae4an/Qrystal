package com.qrystal.app.question.dto;

import com.qrystal.app.question.domain.QuestionStatus;
import com.qrystal.app.question.model.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {
    private Long id;
    private String title;
    private String categoryName;
    private Long typeId;
    private String typeName;
    private String userName;
    private Integer difficulty;
    private Boolean isPublic;
    private QuestionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String content;
    private String answer;
    private String explanation;
    private List<QuestionChoiceResponse> choices;
    private List<String> tags;

    public static QuestionResponse from(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .title(question.getTitle())
                .categoryName(question.getCategoryName())
                .typeId(question.getTypeId())
                .typeName(question.getTypeName())
                .userName(question.getUserName())
                .difficulty(question.getDifficulty())
                .isPublic(question.getIsPublic())
                .status(question.getStatus())
                .content(question.getContent())
                .answer(question.getAnswer())
                .explanation(question.getExplanation())
                .choices(question.getChoices().stream()
                        .map(QuestionChoiceResponse::from)
                        .collect(Collectors.toList()))
                .tags(question.getTags())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .build();
    }
}
