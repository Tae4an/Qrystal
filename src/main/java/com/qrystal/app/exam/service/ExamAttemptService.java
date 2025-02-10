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
                        .questionTypeId(dto.getQuestionTypeId())
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
        log.info("시험 제출 시작 - attemptId: {}, userId: {}", attemptId, userId);

        ExamAttempt attempt = validateAndGetAttempt(attemptId, userId);
        log.info("시험 응시 정보 검증 완료");

        // 시간 만료 체크
        if (examTimerService.isExamExpired(attemptId)) {
            log.warn("시험 시간 만료 - attemptId: {}", attemptId);
            handleExamTimeout(attempt);
            throw new CustomException(ErrorCode.EXAM_TIME_EXPIRED);
        }

        try {
            // 답안 저장
            log.info("답안 저장 시작 - 답안 개수: {}", answers.size());
            saveAnswers(attemptId, answers, userId);
            log.info("답안 저장 완료");

            // 자동 채점 처리
            log.info("자동 채점 시작");
            int totalScore = performAutoGrading(attemptId);
            log.info("자동 채점 완료 - 총점: {}", totalScore);

            // 시험 상태 업데이트
            attempt.setStatus(ExamAttemptStatus.SUBMITTED);
            attempt.setSubmittedAt(LocalDateTime.now());
            attempt.setTotalScore(totalScore);

            examAttemptMapper.updateSubmission(attemptId,
                    attempt.getSubmittedAt(),
                    totalScore);
            log.info("시험 제출 상태 업데이트 완료");

            // Redis 타이머 제거
            examTimerService.stopExamTimer(attemptId);
            log.info("시험 타이머 제거 완료");

            return ExamAttemptResponseDto.from(attempt);
        } catch (Exception e) {
            log.error("시험 제출 중 오류 발생", e);
            throw e;
        }
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
        if (exam.getStatus() != ExamStatus.ACTIVE) {
            throw new CustomException(ErrorCode.EXAM_NOT_AVAILABLE);
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

        ExamAttemptResponseDto responseDto = ExamAttemptResponseDto.from(attempt);

        if (responseDto.getAnswers() != null && !responseDto.getAnswers().isEmpty()) {
            // 정답 수 계산
            long correctCount = responseDto.getAnswers().stream()
                    .filter(answer -> Boolean.TRUE.equals(answer.getIsCorrect()))
                    .count();

            log.debug("정답 개수: {}", correctCount);

            // 전체 문제 수
            int totalQuestions = responseDto.getAnswers().size();
            log.debug("전체 문제 수: {}", totalQuestions);

            // 정답률 계산
            double correctRate = ((double) correctCount / totalQuestions) * 100;
            log.debug("계산된 정답률: {}", correctRate);

            double roundedRate = Math.round(correctRate * 10.0) / 10.0;
            log.debug("반올림된 정답률: {}", roundedRate);

            responseDto.setCorrectRate(roundedRate);
            responseDto.setCorrectCount((int) correctCount);
            responseDto.setWrongCount(totalQuestions - (int) correctCount);
        } else {
            log.warn("답안이 없거나 비어있습니다. attemptId: {}", attemptId);
        }

        return responseDto;
    }

    // 자동 채점 수행
    private int performAutoGrading(Long attemptId) {
        ExamAttempt attempt = examAttemptMapper.findById(attemptId);
        List<ExamAnswer> answers = examAnswerMapper.findByAttemptId(attemptId);
        return autoGradingService.gradeExamAttempt(attempt.getExamId(), answers);
    }
}