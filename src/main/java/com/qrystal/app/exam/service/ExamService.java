package com.qrystal.app.exam.service;

import com.qrystal.app.exam.domain.ExamStatus;
import com.qrystal.app.exam.dto.ExamCreateRequest;
import com.qrystal.app.exam.dto.ExamSearchCondition;
import com.qrystal.app.exam.mapper.ExamMapper;
import com.qrystal.app.exam.model.Exam;
import com.qrystal.app.exam.model.ExamQuestion;
import com.qrystal.app.question.service.QuestionService;
import com.qrystal.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.qrystal.exception.ErrorCode;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamService {
    private final ExamMapper examMapper;
    private final QuestionService questionService;

    @Transactional
    public Long createExam(ExamCreateRequest request, Long userId) {
        // 1. 시험지 기본 정보 저장
        Exam exam = request.toEntity();
        exam.setCreatedBy(userId);
        exam.setStatus(ExamStatus.ACTIVE);
        examMapper.save(exam);

        // 2. ExamQuestionRequest를 ExamQuestion으로 변환 후 저장
        if (request.getQuestions() != null && !request.getQuestions().isEmpty()) {
            List<ExamQuestion> examQuestions = request.getQuestions().stream()
                    .map(req -> {
                        ExamQuestion eq = new ExamQuestion();
                        eq.setQuestionId(req.getQuestionId());
                        eq.setQuestionNumber(req.getQuestionNumber());
                        eq.setPoint(req.getPoint());
                        return eq;
                    })
                    .collect(Collectors.toList());

            examMapper.saveQuestions(exam.getId(), examQuestions);
        }

        return exam.getId();
    }
    // 내 모의고사 목록
    public List<Exam> getMyExams(Long userId) {
        return examMapper.findAll(ExamSearchCondition.builder()
                .createdBy(userId)
                .status(ExamStatus.ACTIVE)
                .build());
    }
    
    // 공개된 모의고사 목록
    public List<Exam> getPublicExams() {
        return examMapper.findAll(ExamSearchCondition.builder()
                .isPublic(true)
                .status(ExamStatus.ACTIVE)
                .build());
    }
    
    // 상세 조회
    public Exam getExam(Long id) {
        Exam exam = examMapper.findById(id);
        if (exam == null) {
            throw new CustomException(ErrorCode.EXAM_NOT_FOUND);
        }else if (exam.getStatus() == ExamStatus.INACTIVE) {
            throw new CustomException(ErrorCode.EXAM_NOT_AVAILABLE);
        }
        return exam;
    }

    // 시험지 수정
    @Transactional
    public void updateExam(Long id, ExamCreateRequest request, Long userId) {
        Exam exam = getExam(id);

        // 권한 체크
        if (!exam.getCreatedBy().equals(userId)) {
            throw new CustomException(ErrorCode.EXAM_ACCESS_DENIED);
        }

        // 기본 정보 업데이트
        Exam updateExam = request.toEntity();
        updateExam.setId(id);
        examMapper.update(updateExam);

        // 문제 업데이트
        examMapper.deleteQuestions(id);
        if (request.getQuestions() != null && !request.getQuestions().isEmpty()) {
            List<ExamQuestion> examQuestions = request.getQuestions().stream()
                    .map(req -> {
                        ExamQuestion eq = new ExamQuestion();
                        eq.setQuestionId(req.getQuestionId());
                        eq.setQuestionNumber(req.getQuestionNumber());
                        eq.setPoint(req.getPoint());
                        return eq;
                    })
                    .collect(Collectors.toList());
            examMapper.saveQuestions(id, examQuestions);
        }
    }

    // 시험지 삭제
    @Transactional
    public void deleteExam(Long id, Long userId) {
        Exam exam = getExam(id);

        // 권한 체크
        if (!exam.getCreatedBy().equals(userId)) {
            throw new CustomException(ErrorCode.EXAM_ACCESS_DENIED);
        }

        examMapper.updateStatus(id, ExamStatus.INACTIVE);
    }

    // 카테고리별 공개된 모의고사 조회
    public List<Exam> getExamsByCategory(Long categoryId) {
        return examMapper.findAll(ExamSearchCondition.builder()
                .categoryId(categoryId)
                .isPublic(true)
                .status(ExamStatus.ACTIVE)
                .build());
    }
}