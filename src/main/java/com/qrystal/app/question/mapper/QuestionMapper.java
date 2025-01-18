package com.qrystal.app.question.mapper;

import com.qrystal.app.question.domain.QuestionStatus;
import com.qrystal.app.question.dto.QuestionSearchCondition;
import com.qrystal.app.question.model.Question;
import com.qrystal.app.question.model.QuestionChoice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface QuestionMapper {
    // 문제 CRUD
    List<Question> findAll(QuestionSearchCondition condition);
    Question findById(Long id);
    void save(Question question);
    void update(Question question);
    void delete(Long id);
    
    // 문제 상태 관리
    void updateStatus(@Param("id") Long id, @Param("status") QuestionStatus status);
    
    // 객관식 보기 관리
    void saveChoices(@Param("questionId") Long questionId, @Param("choices") List<QuestionChoice> choices);
    void deleteChoices(Long questionId);
    List<QuestionChoice> findChoicesByQuestionId(Long questionId);
    
    // 태그 관리
    void saveTags(@Param("questionId") Long questionId, @Param("tags") List<String> tags);
    void deleteTags(Long questionId);
    List<String> findTagsByQuestionId(Long questionId);
}