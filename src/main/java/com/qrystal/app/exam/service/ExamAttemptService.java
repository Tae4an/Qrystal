package com.qrystal.app.exam.service;

import com.qrystal.app.exam.domain.ExamAttemptStatus;
import com.qrystal.app.exam.domain.ExamStatus;
import com.qrystal.app.exam.dto.ExamAnswerSubmitDto;
import com.qrystal.app.exam.dto.ExamAttemptCreateDto;
import com.qrystal.app.exam.dto.ExamAttemptResponseDto;
import com.qrystal.app.exam.mapper.ExamAnswerMapper;
import com.qrystal.app.exam.mapper.ExamAttemptMapper;
import com.qrystal.app.exam.model.Exam;
import com.qrystal.app.exam.model.ExamAnswer;
import com.qrystal.app.exam.model.ExamAttempt;
import com.qrystal.exception.CustomException;
import com.qrystal.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ExamAttemptService {
    private final ExamAttemptMapper examAttemptMapper;
    private final ExamAnswerMapper examAnswerMapper;
    private final ExamService examService;
    private final ExamTimerService examTimerService;

    // 시험 응시 시작
    @Transactional
    public ExamAttemptResponseDto startExam(ExamAttemptCreateDto createDto, Long userId) {
        // 1. 시험 정보 조회 및 검증
        Exam exam = examService.getExam(createDto.getExamId());
        validateExamStartable(exam, userId);

        // 2. 이미 진행중인 시험이 있는지 확인
        ExamAttempt existingAttempt = examAttemptMapper.findByExamIdAndUserId(createDto.getExamId(), userId);
        if (existingAttempt != null && existingAttempt.getStatus() == ExamAttemptStatus.IN_PROGRESS) {
            throw new CustomException(ErrorCode.EXAM_ALREADY_IN_PROGRESS);
        }

        // 3. 시험 응시 정보 생성
        ExamAttempt attempt = createDto.toEntity(userId, exam);
        examAttemptMapper.save(attempt);

        // 4. Redis에 시험 타이머 설정
        try {
            examTimerService.startExamTimer(attempt.getId(), attempt.getTimeLimit());
        } catch (Exception e) {
            log.error("Failed to start exam timer", e);
            throw new CustomException(ErrorCode.EXAM_TIMER_ERROR);
        }

        return ExamAttemptResponseDto.from(attempt);
    }

    // 진행 중인 시험 조회
    public ExamAttemptResponseDto getCurrentExam(Long userId) {
        List<ExamAttempt> attempts = examAttemptMapper.findByUserId(userId);
        ExamAttempt currentAttempt = attempts.stream()
                .filter(a -> a.getStatus() == ExamAttemptStatus.IN_PROGRESS)
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.EXAM_NOT_IN_PROGRESS));

        // 시간 만료 체크
        if (examTimerService.isExamExpired(currentAttempt.getId())) {
            handleExamTimeout(currentAttempt);
            throw new CustomException(ErrorCode.EXAM_TIME_EXPIRED);
        }

        return ExamAttemptResponseDto.from(currentAttempt);
    }

    // 답안 제출
    @Transactional
    public void submitExam(Long attemptId, List<ExamAnswerSubmitDto> answers) {
        // 1. 시험 응시 정보 조회 및 검증
        ExamAttempt attempt = examAttemptMapper.findById(attemptId);
        validateExamSubmittable(attempt);

        // 2. 답안 저장
        List<ExamAnswer> examAnswers = answers.stream()
                .map(dto -> {
                    ExamAnswer answer = new ExamAnswer();
                    answer.setAttemptId(attemptId);
                    answer.setQuestionId(dto.getQuestionId());
                    answer.setSubmittedAnswer(dto.getSubmittedAnswer());
                    // 추후 채점을 위한 초기값 설정
                    answer.setIsGraded(false);
                    return answer;
                })
                .collect(Collectors.toList());

        examAnswerMapper.saveAll(attemptId, examAnswers);

        // 3. 시험 상태 업데이트
        attempt.setStatus(ExamAttemptStatus.SUBMITTED);
        attempt.setSubmittedAt(LocalDateTime.now());
        examAttemptMapper.updateSubmission(attemptId, attempt.getSubmittedAt(), null);

        // 4. Redis 타이머 제거
        examTimerService.stopExamTimer(attemptId);
    }

    // 시험 응시 가능 여부 검증
    private void validateExamStartable(Exam exam, Long userId) {
        if (exam.getStatus() != ExamStatus.PUBLISHED) {
            throw new CustomException(ErrorCode.EXAM_NOT_PUBLISHED);
        }
        if (!exam.isPublic() && !exam.getCreatedBy().equals(userId)) {
            throw new CustomException(ErrorCode.EXAM_ACCESS_DENIED);
        }
    }

    // 답안 제출 가능 여부 검증
    private void validateExamSubmittable(ExamAttempt attempt) {
        if (attempt == null) {
            throw new CustomException(ErrorCode.EXAM_NOT_FOUND);
        }
        if (attempt.getStatus() != ExamAttemptStatus.IN_PROGRESS) {
            throw new CustomException(ErrorCode.EXAM_NOT_IN_PROGRESS);
        }
        if (examTimerService.isExamExpired(attempt.getId())) {
            handleExamTimeout(attempt);
            throw new CustomException(ErrorCode.EXAM_TIME_EXPIRED);
        }
    }

    // 시험 시간 초과 처리
    @Transactional
    public void handleExamTimeout(ExamAttempt attempt) {
        attempt.setStatus(ExamAttemptStatus.TIMEOUT);
        attempt.setIsTimeExpired(true);
        examAttemptMapper.updateStatus(attempt.getId(), ExamAttemptStatus.TIMEOUT);
        examTimerService.stopExamTimer(attempt.getId());
    }

    // 시험 결과 조회
    public ExamAttemptResponseDto getExamResult(Long attemptId) {
        ExamAttempt attempt = examAttemptMapper.findById(attemptId);
        if (attempt == null) {
            throw new CustomException(ErrorCode.EXAM_NOT_FOUND);
        }
        return ExamAttemptResponseDto.from(attempt);
    }
}