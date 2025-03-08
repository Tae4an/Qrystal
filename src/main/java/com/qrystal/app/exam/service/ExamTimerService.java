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
    private final RedisTemplate<String, String> redisTemplate;  // Object 대신 String 사용
    private static final String KEY_PREFIX = "exam:attempt:";
    
    // 시험 시작 시 타이머 설정
    public void startExamTimer(Long attemptId, Integer timeLimit) {
        String key = KEY_PREFIX + attemptId;
        try {
            LocalDateTime endTime = LocalDateTime.now().plusMinutes(timeLimit);
            redisTemplate.opsForValue().set(key, endTime.toString(), timeLimit, TimeUnit.MINUTES);
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
            String endTimeStr = redisTemplate.opsForValue().get(key);
            if (endTimeStr == null) {
                return 0L;
            }
            LocalDateTime endTime = LocalDateTime.parse(endTimeStr);
            return ChronoUnit.SECONDS.between(LocalDateTime.now(), endTime);
        } catch (Exception e) {
            log.error("Failed to get remaining time for attemptId: {}", attemptId, e);
            return 0L; // 에러 발생 시 0 반환
        }
    }
    
    // 시험 종료 처리
    public void stopExamTimer(Long attemptId) {
        String key = KEY_PREFIX + attemptId;
        try {
            Boolean deleted = redisTemplate.delete(key);
            if (deleted == null || !deleted) {
                log.warn("Timer key not found for attemptId: {}", attemptId);
            } else {
                log.info("Successfully removed timer for attemptId: {}", attemptId);
            }
        } catch (Exception e) {
            log.error("Failed to stop exam timer for attemptId: {}", attemptId, e);
        }
    }

    // 시험 시간 만료 여부 확인
    public boolean isExamExpired(Long attemptId) {
        try {
            return getRemainingTime(attemptId) <= 0;
        } catch (Exception e) {
            log.error("Failed to check exam expiration for attemptId: {}", attemptId, e);
            return true; // 에러 발생 시 만료된 것으로 처리
        }
    }
}