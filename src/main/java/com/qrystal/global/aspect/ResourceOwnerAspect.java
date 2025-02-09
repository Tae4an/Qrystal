package com.qrystal.global.aspect;


import com.qrystal.app.exam.service.ExamService;
import com.qrystal.app.question.service.QuestionService;
import com.qrystal.app.user.service.UserService;
import com.qrystal.exception.CustomException;
import com.qrystal.exception.ErrorCode;
import com.qrystal.global.annotation.ResourceOwner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ResourceOwnerAspect {
    private final ExamService examService;
    private final QuestionService questionService;
    private final UserService userService;

    @Before("@annotation(resourceOwner)")
    public void checkResourceOwnership(JoinPoint joinPoint, ResourceOwner resourceOwner) {
        // 1. 현재 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String email = userService.extractEmail(authentication);
        Long userId = userService.getUserByEmail(email).getId();

        // 2. 리소스 ID 추출
        Long resourceId = extractResourceId(joinPoint, resourceOwner.idParameter());

        // 3. 리소스 소유권 검증
        validateResourceOwner(joinPoint, resourceId, userId);
    }

    private Long extractResourceId(JoinPoint joinPoint, String parameterName) {
        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String[] parameterNames = signature.getParameterNames();
            Object[] args = joinPoint.getArgs();

            for (int i = 0; i < parameterNames.length; i++) {
                if (parameterNames[i].equals(parameterName)) {
                    if (args[i] instanceof Long) {
                        return (Long) args[i];
                    } else if (args[i] instanceof String) {
                        return Long.parseLong((String) args[i]);
                    }
                }
            }
            throw new CustomException(ErrorCode.INVALID_INPUT);
        } catch (Exception e) {
            log.error("리소스 ID 추출 실패", e);
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }
    }

    private void validateResourceOwner(JoinPoint joinPoint, Long resourceId, Long userId) {
        // 메서드가 속한 클래스의 이름으로 리소스 타입 판단
        String className = joinPoint.getTarget().getClass().getSimpleName();

        try {
            if (className.contains("Exam")) {
                var exam = examService.getExam(resourceId);
                if (!exam.getCreatedBy().equals(userId)) {
                    throw new CustomException(ErrorCode.RESOURCE_ACCESS_DENIED);
                }
            } else if (className.contains("Question")) {
                var question = questionService.getQuestion(resourceId);
                if (!question.getUserId().equals(userId)) {
                    throw new CustomException(ErrorCode.RESOURCE_ACCESS_DENIED);
                }
            }
        } catch (CustomException e) {
            if (e.getErrorCode() == ErrorCode.EXAM_NOT_FOUND ||
                    e.getErrorCode() == ErrorCode.QUESTION_NOT_FOUND) {
                throw e;
            }
            throw new CustomException(ErrorCode.RESOURCE_ACCESS_DENIED);
        }
    }
}