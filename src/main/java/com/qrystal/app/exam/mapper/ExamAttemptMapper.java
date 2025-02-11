package com.qrystal.app.exam.mapper;

import com.qrystal.app.exam.domain.ExamAttemptStatus;
import com.qrystal.app.exam.model.ExamAttempt;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ExamAttemptMapper {
    // 시험 응시 CRUD
    void save(ExamAttempt attempt);
    ExamAttempt findById(Long id);
    ExamAttempt findByExamIdAndUserId(Long examId, Long userId);
    List<ExamAttempt> findByUserId(Long userId);
    void update(ExamAttempt attempt);
    
    // 상태 업데이트
    void updateStatus(@Param("id") Long id,
                     @Param("status") ExamAttemptStatus status);
    
    // 시험 완료 처리
    void updateSubmission(@Param("id") Long id, 
                         @Param("submittedAt") LocalDateTime submittedAt,
                         @Param("totalScore") Integer totalScore);

    void delete(Long id);
}