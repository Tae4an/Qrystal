package com.qrystal.app.exam.controller;

import com.qrystal.app.exam.domain.ExamStatus;
import com.qrystal.app.exam.dto.*;
import com.qrystal.app.exam.model.Exam;
import com.qrystal.app.exam.service.ExamAttemptService;
import com.qrystal.app.exam.service.ExamService;
import com.qrystal.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {
    private final ExamService examService;
    private final ExamAttemptService examAttemptService;
    private final UserService userService;

    // 모의고사 생성
    @PostMapping
    public ResponseEntity<Long> createExam(
            @RequestBody ExamCreateRequest request,
            Principal principal) {
        try {
            log.debug("Principal in createExam: {}", principal);
            String email = userService.extractEmail(principal);
            Long userId = userService.getUserByEmail(email).getId();
            Long examId = examService.createExam(request, userId);
            return ResponseEntity.ok(examId);
        } catch (Exception e) {
            log.error("모의고사 생성 실패", e);
            throw new RuntimeException(e);
        }
    }

    // 내 모의고사 목록
    @GetMapping("/my")
    public ResponseEntity<List<ExamResponse>> getMyExams(Principal principal) {
        String email = userService.extractEmail(principal);
        Long userId = userService.getUserByEmail(email).getId();
        List<Exam> exams = examService.getMyExams(userId);
        List<ExamResponse> response = exams.stream()
                .map(ExamResponse::of)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // 공개된 모의고사 목록
    @GetMapping("/public")
    public ResponseEntity<List<ExamResponse>> getPublicExams() {
        try {
            List<Exam> exams = examService.getPublicExams();
            List<ExamResponse> response = exams.stream()
                    .map(ExamResponse::of)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("공개 모의고사 목록 조회 실패", e);
            throw e;
        }
    }
    // 모의고사 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ExamResponse> getExam(@PathVariable Long id) {
        Exam exam = examService.getExam(id);
        return ResponseEntity.ok(ExamResponse.of(exam));
    }

    // 카테고리별 모의고사 조회
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ExamResponse>> getExamsByCategory(
            @PathVariable Long categoryId) {
        List<Exam> exams = examService.getExamsByCategory(categoryId);
        List<ExamResponse> response = exams.stream()
                .map(ExamResponse::of)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // 시험지 수정
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateExam(
            @PathVariable Long id,
            @RequestBody ExamCreateRequest request,
            Principal principal) {
        String email = userService.extractEmail(principal);
        Long userId = userService.getUserByEmail(email).getId();
        examService.updateExam(id, request, userId);
        return ResponseEntity.ok().build();
    }

    // 시험지 상태 변경
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateExamStatus(
            @PathVariable Long id,
            @RequestParam ExamStatus status,
            Principal principal) {
        String email = userService.extractEmail(principal);
        Long userId = userService.getUserByEmail(email).getId();
        examService.updateExamStatus(id, status, userId);
        return ResponseEntity.ok().build();
    }

    // 시험지 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(
            @PathVariable Long id,
            Principal principal) {
        String email = userService.extractEmail(principal);
        Long userId = userService.getUserByEmail(email).getId();
        examService.deleteExam(id, userId);
        return ResponseEntity.ok().build();
    }
    // 시험 시작
    @PostMapping("/{examId}/start")
    public ResponseEntity<Long> startExam(@PathVariable Long examId, Principal principal) {
        String email = userService.extractEmail(principal);
        Long userId = userService.getUserByEmail(email).getId();

        ExamAttemptCreateDto createDto = new ExamAttemptCreateDto();
        createDto.setExamId(examId);

        ExamAttemptResponseDto attempt = examAttemptService.startExam(createDto, userId);
        return ResponseEntity.ok(attempt.getId());
    }

    // 시험 응시 정보 조회
    @GetMapping("/{examId}/attempts/{attemptId}")
    public ResponseEntity<ExamAttemptResponseDto> getAttempt(
            @PathVariable Long examId,
            @PathVariable Long attemptId,
            Principal principal) {
        String email = userService.extractEmail(principal);
        Long userId = userService.getUserByEmail(email).getId();

        ExamAttemptResponseDto attempt = examAttemptService.getExamAttempt(attemptId, userId);
        return ResponseEntity.ok(attempt);
    }

    // 답안 임시 저장
    @PostMapping("/{examId}/attempts/{attemptId}/save")
    public ResponseEntity<Void> saveAnswers(
            @PathVariable Long examId,
            @PathVariable Long attemptId,
            @RequestBody List<ExamAnswerSubmitDto> answers,
            Principal principal) {
        String email = userService.extractEmail(principal);
        Long userId = userService.getUserByEmail(email).getId();

        examAttemptService.saveAnswers(attemptId, answers, userId);
        return ResponseEntity.ok().build();
    }

    // 답안 최종 제출
    @PostMapping("/{examId}/attempts/{attemptId}/submit")
    public ResponseEntity<ExamAttemptResponseDto> submitExam(
            @PathVariable Long examId,
            @PathVariable Long attemptId,
            @RequestBody List<ExamAnswerSubmitDto> answers,
            Principal principal) {
        String email = userService.extractEmail(principal);
        Long userId = userService.getUserByEmail(email).getId();

        ExamAttemptResponseDto result = examAttemptService.submitExam(attemptId, answers, userId);
        return ResponseEntity.ok(result);
    }
}