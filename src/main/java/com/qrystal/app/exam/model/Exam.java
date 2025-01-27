package com.qrystal.app.exam.model;

import com.qrystal.app.exam.domain.ExamStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Exam {
    private Long id;
    private String title;           // 시험지명
    private String description;     // 시험 설명
    private int timeLimit;         // 제한시간(분)
    private int totalPoints;       // 총점
    private Long createdBy;        // 작성자
    private Long categoryId;       // 카테고리
    private ExamStatus status;     // DRAFT, PUBLISHED, CLOSED
    private boolean isPublic;      // 공개 여부
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 추가 필드
    private String categoryName;    // 카테고리명
    private String createdByName;   // 작성자명
    private List<ExamQuestion> questions = new ArrayList<>(); // 시험 문제 목록
}
