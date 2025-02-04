package com.qrystal.app.exam.mapper;

import com.qrystal.app.exam.model.ExamAnswer;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface ExamAnswerMapper {
    // 답안 CRUD
    void save(ExamAnswer answer);
    void saveAll(@Param("attemptId") Long attemptId,
                @Param("answers") List<ExamAnswer> answers);
    List<ExamAnswer> findByAttemptId(Long attemptId);
    void update(ExamAnswer answer);
    
    // 채점 결과 업데이트
    void updateGrading(@Param("id") Long id,
                      @Param("isCorrect") Boolean isCorrect,
                      @Param("score") Integer score,
                      @Param("gradingComment") String gradingComment);
    void deleteByAttemptId(Long attemptId);
}