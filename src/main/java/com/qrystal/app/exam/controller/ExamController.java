package com.qrystal.app.exam.controller;

import com.qrystal.app.exam.domain.ExamStatus;
import com.qrystal.app.exam.dto.ExamCreateRequest;
import com.qrystal.app.exam.dto.ExamResponse;
import com.qrystal.app.exam.model.Exam;
import com.qrystal.app.exam.service.ExamService;
import com.qrystal.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {
    private final ExamService examService;
    private final UserService userService;

    // 모의고사 생성
    @PostMapping
    public ResponseEntity<Long> createExam(
            @RequestBody ExamCreateRequest request,
            @AuthenticationPrincipal String email) {
        Long userId = userService.getUserByEmail(email).getId();
        Long examId = examService.createExam(request, userId);
        return ResponseEntity.ok(examId);
    }

    // 내 모의고사 목록
    @GetMapping("/my")
    public ResponseEntity<List<ExamResponse>> getMyExams(
            @AuthenticationPrincipal String email) {
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
            @AuthenticationPrincipal String email) {
        Long userId = userService.getUserByEmail(email).getId();
        examService.updateExam(id, request, userId);
        return ResponseEntity.ok().build();
    }

    // 시험지 상태 변경
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateExamStatus(
            @PathVariable Long id,
            @RequestParam ExamStatus status,
            @AuthenticationPrincipal String email) {
        Long userId = userService.getUserByEmail(email).getId();
        examService.updateExamStatus(id, status, userId);
        return ResponseEntity.ok().build();
    }

    // 시험지 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(
            @PathVariable Long id,
            @AuthenticationPrincipal String email) {
        Long userId = userService.getUserByEmail(email).getId();
        examService.deleteExam(id, userId);
        return ResponseEntity.ok().build();
    }
}