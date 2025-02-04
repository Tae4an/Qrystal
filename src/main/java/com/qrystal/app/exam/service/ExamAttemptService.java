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
    private final AutoGradingService autoGradingService;
    // 시험 시작
    @Transactional
    public ExamAttemptResponseDto startExam(ExamAttemptCreateDto createDto, Long userId) {
        // 1. 시험 정보 조회 및 검증
        Exam exam = examService.getExam(createDto.getExamId());
        validateExamStartable(exam, userId);

        // 2. 진행중인 시험이 있는지 확인
        ExamAttempt existingAttempt = examAttemptMapper.findByExamIdAndUserId(
                createDto.getExamId(), userId);

        if (existingAttempt != null &&
                existingAttempt.getStatus() == ExamAttemptStatus.IN_PROGRESS) {
            throw new CustomException(ErrorCode.EXAM_ALREADY_IN_PROGRESS);
        }

        // 3. 새로운 시험 응시 정보 생성
        ExamAttempt attempt = ExamAttempt.builder()
                .examId(exam.getId())
                .userId(userId)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusMinutes(exam.getTimeLimit()))
                .timeLimit(exam.getTimeLimit())
                .status(ExamAttemptStatus.IN_PROGRESS)
                .isTimeExpired(false)
                .build();

        examAttemptMapper.save(attempt);

        // 4. Redis에 시험 타이머 설정
        examTimerService.startExamTimer(attempt.getId(), attempt.getTimeLimit());

        return ExamAttemptResponseDto.from(attempt);
    }

    // 시험 응시 정보 조회
    public ExamAttemptResponseDto getExamAttempt(Long attemptId, Long userId) {
        ExamAttempt attempt = examAttemptMapper.findById(attemptId);
        if (attempt == null) {
            throw new CustomException(ErrorCode.EXAM_ATTEMPT_NOT_FOUND);
        }

        // 권한 체크
        if (!attempt.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.EXAM_ACCESS_DENIED);
        }

        // 시간 만료 체크
        if (attempt.getStatus() == ExamAttemptStatus.IN_PROGRESS &&
                examTimerService.isExamExpired(attemptId)) {
            handleExamTimeout(attempt);
        }

        return ExamAttemptResponseDto.from(attempt);
    }

    // 답안 임시 저장
    @Transactional
    public void saveAnswers(Long attemptId, List<ExamAnswerSubmitDto> answers, Long userId) {
        ExamAttempt attempt = validateAndGetAttempt(attemptId, userId);

        // 기존 답안 삭제
        examAnswerMapper.deleteByAttemptId(attemptId);

        // 새로운 답안 저장
        List<ExamAnswer> examAnswers = answers.stream()
                .map(dto -> ExamAnswer.builder()
                        .attemptId(attemptId)
                        .questionId(dto.getQuestionId())
                        .submittedAnswer(dto.getSubmittedAnswer())
                        .isGraded(false)
                        .build())
                .collect(Collectors.toList());

        if (!examAnswers.isEmpty()) {
            examAnswerMapper.saveAll(attemptId, examAnswers);
        }
    }

    // 답안 최종 제출
    @Transactional
    public ExamAttemptResponseDto submitExam(Long attemptId,
                                             List<ExamAnswerSubmitDto> answers,
                                             Long userId) {
        ExamAttempt attempt = validateAndGetAttempt(attemptId, userId);

        // 시간 만료 체크
        if (examTimerService.isExamExpired(attemptId)) {
            handleExamTimeout(attempt);
            throw new CustomException(ErrorCode.EXAM_TIME_EXPIRED);
        }

        // 답안 저장
        saveAnswers(attemptId, answers, userId);

        // 자동 채점 처리
        int totalScore = performAutoGrading(attemptId);

        // 시험 상태 업데이트
        attempt.setStatus(ExamAttemptStatus.SUBMITTED);
        attempt.setSubmittedAt(LocalDateTime.now());
        attempt.setTotalScore(totalScore);

        examAttemptMapper.updateSubmission(attemptId,
                attempt.getSubmittedAt(),
                totalScore);

        // Redis 타이머 제거
        examTimerService.stopExamTimer(attemptId);

        return ExamAttemptResponseDto.from(attempt);
    }

    // 검증 및 attempt 조회 헬퍼 메서드
    private ExamAttempt validateAndGetAttempt(Long attemptId, Long userId) {
        ExamAttempt attempt = examAttemptMapper.findById(attemptId);
        if (attempt == null) {
            throw new CustomException(ErrorCode.EXAM_ATTEMPT_NOT_FOUND);
        }

        if (!attempt.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.EXAM_ACCESS_DENIED);
        }

        if (attempt.getStatus() != ExamAttemptStatus.IN_PROGRESS) {
            throw new CustomException(ErrorCode.EXAM_NOT_IN_PROGRESS);
        }

        return attempt;
    }

    // 시험 제출 가능 여부 검증
    private void validateExamStartable(Exam exam, Long userId) {
        if (exam.getStatus() != ExamStatus.PUBLISHED) {
            throw new CustomException(ErrorCode.EXAM_NOT_PUBLISHED);
        }
        if (!exam.isPublic() && !exam.getCreatedBy().equals(userId)) {
            throw new CustomException(ErrorCode.EXAM_ACCESS_DENIED);
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
    public ExamAttemptResponseDto getExamResult(Long attemptId) {
        ExamAttempt attempt = examAttemptMapper.findById(attemptId);
        if (attempt == null) {
            throw new CustomException(ErrorCode.EXAM_ATTEMPT_NOT_FOUND);
        }

        // 아직 제출되지 않은 시험 결과는 조회 불가
        if (attempt.getStatus() == ExamAttemptStatus.IN_PROGRESS) {
            throw new CustomException(ErrorCode.EXAM_NOT_SUBMITTED);
        }

        return ExamAttemptResponseDto.from(attempt);
    }

    // 자동 채점 수행
    private int performAutoGrading(Long attemptId) {
        List<ExamAnswer> answers = examAnswerMapper.findByAttemptId(attemptId);
        return autoGradingService.gradeExamAttempt(attemptId, answers);
    }
}