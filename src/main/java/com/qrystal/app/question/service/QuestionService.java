package com.qrystal.app.question.service;

import com.qrystal.app.question.domain.QuestionStatus;
import com.qrystal.app.question.dto.QuestionSearchCondition;
import com.qrystal.app.question.mapper.QuestionMapper;
import com.qrystal.app.question.model.Question;
import com.qrystal.exception.CustomException;
import com.qrystal.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionService {
    
    private final QuestionMapper questionMapper;

    // 문제 목록 조회
    public List<Question> getQuestions(QuestionSearchCondition condition) {
        Boolean isPublic = condition.getIsPublic() != null ? condition.getIsPublic() : true;

        condition = QuestionSearchCondition.builder()
                .isPublic(isPublic)
                .status(QuestionStatus.ACTIVE)  // 항상 ACTIVE 상태만 조회
                .build();

        return questionMapper.findAll(condition);
    }

    // 문제 상세 조회
    public Question getQuestion(Long id) {
        Question question = questionMapper.findById(id);
        if (question == null) {
            throw new CustomException(ErrorCode.QUESTION_NOT_FOUND);
        }
        return question;
    }

    // 문제 생성
    @Transactional
    public Question createQuestion(Question question) {
        // 상태 설정
        question.setStatus(QuestionStatus.ACTIVE);
        
        // 문제 저장
        questionMapper.save(question);
        
        // 객관식 보기가 있는 경우
        if (question.getChoices() != null && !question.getChoices().isEmpty()) {
            questionMapper.saveChoices(question.getId(), question.getChoices());
        }
        
        // 태그가 있는 경우
        if (question.getTags() != null && !question.getTags().isEmpty()) {
            questionMapper.saveTags(question.getId(), question.getTags());
        }
        
        return getQuestion(question.getId());
    }

    // 문제 수정
    @Transactional
    public Question updateQuestion(Long id, Question updateQuestion) {
        Question question = getQuestion(id);
        
        // 기본 정보 업데이트
        updateQuestion.setId(id);
        questionMapper.update(updateQuestion);
        
        // 객관식 보기 업데이트
        if (updateQuestion.getChoices() != null) {
            questionMapper.deleteChoices(id);
            if (!updateQuestion.getChoices().isEmpty()) {
                questionMapper.saveChoices(id, updateQuestion.getChoices());
            }
        }
        
        // 태그 업데이트
        if (updateQuestion.getTags() != null) {
            questionMapper.deleteTags(id);
            if (!updateQuestion.getTags().isEmpty()) {
                questionMapper.saveTags(id, updateQuestion.getTags());
            }
        }
        
        return getQuestion(id);
    }

    // 문제 삭제
    @Transactional
    public void deleteQuestion(Long id) {
        questionMapper.updateStatus(id, QuestionStatus.INACTIVE);
    }

    // 문제 상태 변경
    @Transactional
    public void updateQuestionStatus(Long id, QuestionStatus status) {
        Question question = getQuestion(id);
        questionMapper.updateStatus(id, status);
    }

    // 내 문제 목록 조회
    public List<Question> getMyQuestions(Long userId) {
        return questionMapper.findAll(QuestionSearchCondition.builder()
                .userId(userId)
                .status(QuestionStatus.ACTIVE)  // ACTIVE 상태만 조회
                .build());
    }

    // 카테고리별 문제 목록 조회
    public List<Question> getQuestionsByCategory(Long categoryId) {
        return questionMapper.findAll(QuestionSearchCondition.builder()
                .categoryId(categoryId)
                .isPublic(true)
                .status(QuestionStatus.ACTIVE)
                .build());
    }
}