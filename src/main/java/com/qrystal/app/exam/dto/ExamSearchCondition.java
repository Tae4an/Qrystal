package com.qrystal.app.exam.dto;

import com.qrystal.app.exam.domain.ExamStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamSearchCondition {
    private Long categoryId;      // 카테고리 검색
    private Long createdBy;       // 작성자 검색
    private ExamStatus status;    // 상태로 검색
    private Boolean isPublic;     // 공개/비공개
    
    // 페이징 처리를 위한 필드 추가 가능
    private Integer page;
    private Integer size;
    
    // 정렬 조건 추가 가능
    private String sortBy;
    private String sortOrder;
}