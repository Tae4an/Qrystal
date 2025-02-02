package com.qrystal.app.exam.service;

import com.qrystal.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.qrystal.exception.ErrorCode;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExamTimerService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String KEY_PREFIX = "exam:attempt:";
    
    // 시험 시작 시 타이머 설정
    public void startExamTimer(Long attemptId, Integer timeLimit) {
        String key = KEY_PREFIX + attemptId;
        LocalDateTime endTime = LocalDateTime.now().plusMinutes(timeLimit);
        try {
            redisTemplate.opsForValue().set(key, endTime, timeLimit, TimeUnit.MINUTES);
            log.info("Exam timer started for attemptId: {}, endTime: {}", attemptId, endTime);
        } catch (Exception e) {
            log.error("Failed to start exam timer for attemptId: {}", attemptId, e);
            throw new CustomException(ErrorCode.EXAM_TIMER_ERROR);
        }
    }
    
    // 남은 시간 조회
    public Long getRemainingTime(Long attemptId) {
        String key = KEY_PREFIX + attemptId;
        try {
            LocalDateTime endTime = (LocalDateTime) redisTemplate.opsForValue().get(key);
            if (endTime == null) {
                return 0L;
            }
            return ChronoUnit.SECONDS.between(LocalDateTime.now(), endTime);
        } catch (Exception e) {
            log.error("Failed to get remaining time for attemptId: {}", attemptId, e);
            throw new CustomException(ErrorCode.EXAM_TIMER_ERROR);
        }
    }
    
    // 시험 종료 처리
    public void stopExamTimer(Long attemptId) {
        String key = KEY_PREFIX + attemptId;
        try {
            redisTemplate.delete(key);
            log.info("Exam timer stopped for attemptId: {}", attemptId);
        } catch (Exception e) {
            log.error("Failed to stop exam timer for attemptId: {}", attemptId, e);
            throw new CustomException(ErrorCode.EXAM_TIMER_ERROR);
        }
    }
    
    // 시험 시간 만료 여부 확인
    public boolean isExamExpired(Long attemptId) {
        return getRemainingTime(attemptId) <= 0;
    }
}