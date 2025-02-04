package com.qrystal.app.exam.mapper;

import com.qrystal.app.exam.domain.ExamStatus;
import com.qrystal.app.exam.dto.ExamSearchCondition;
import com.qrystal.app.exam.model.Exam;
import com.qrystal.app.exam.model.ExamQuestion;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Mapper
public interface ExamMapper {
    // 시험지 CRUD
    void save(Exam exam);
    Exam findById(Long id);
    List<Exam> findAll(ExamSearchCondition condition);
    void update(Exam exam);
    void delete(Long id);
    void updateStatus(@Param("id") Long id, @Param("status") ExamStatus status);

    // 시험 문제 관리
    void saveQuestions(@Param("examId") Long examId, @Param("questions") List<ExamQuestion> questions);
    void deleteQuestions(Long examId);
    List<ExamQuestion> findQuestionsByExamId(Long examId);

    ExamQuestion findQuestionByExamQuestionId(@Param("examId") Long examId,
                                              @Param("questionId") Long questionId);
}