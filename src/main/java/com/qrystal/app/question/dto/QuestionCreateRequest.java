package com.qrystal.app.question.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.util.List;

@Getter
@Setter
public class QuestionCreateRequest {
    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 200, message = "제목은 200자를 초과할 수 없습니다")
    private String title;

    @NotNull(message = "카테고리는 필수입니다")
    private Long categoryId;

    @NotNull(message = "문제 유형은 필수입니다")
    private Long typeId;

    @NotBlank(message = "문제 내용은 필수입니다")
    private String content;

    @NotBlank(message = "정답은 필수입니다")
    private String answer;

    private String explanation;

    @NotNull(message = "난이도는 필수입니다")
    @Min(value = 1, message = "난이도는 1 이상이어야 합니다")
    @Max(value = 5, message = "난이도는 5 이하여야 합니다")
    private Integer difficulty;

    private Boolean isPublic = true;

    private List<QuestionChoiceRequest> choices;
    private List<String> tags;
}


