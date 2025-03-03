package com.qrystal.app.exam.controller;

import com.qrystal.app.exam.dto.*;
import com.qrystal.app.exam.model.Exam;
import com.qrystal.app.exam.service.ExamAttemptService;
import com.qrystal.app.exam.service.ExamService;
import com.qrystal.app.user.service.UserService;
import com.qrystal.exception.CustomException;
import com.qrystal.global.annotation.ResourceOwner;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.qrystal.exception.ErrorCode;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@Slf4j
@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
@Tag(name = "모의고사 관리", description = "모의고사 관련 API")
public class ExamController {
    private final ExamService examService;
    private final ExamAttemptService examAttemptService;
    private final UserService userService;

    @PostMapping
    @Operation(summary = "모의고사 생성", description = "새로운 모의고사를 생성합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 모의고사를 생성함",
            content = @Content(schema = @Schema(implementation = Long.class)))
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

    @GetMapping("/my")
    @Operation(summary = "내 모의고사 목록 조회", description = "사용자의 모의고사 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 모의고사 목록을 반환함",
            content = @Content(schema = @Schema(implementation = ExamResponse.class)))
    public ResponseEntity<List<ExamResponse>> getMyExams(Principal principal) {
        String email = userService.extractEmail(principal);
        Long userId = userService.getUserByEmail(email).getId();
        List<Exam> exams = examService.getMyExams(userId);
        List<ExamResponse> response = exams.stream()
                .map(ExamResponse::of)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/public")
    @Operation(summary = "공개된 모의고사 목록 조회", description = "공개된 모든 모의고사 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 공개 모의고사 목록을 반환함",
            content = @Content(schema = @Schema(implementation = ExamResponse.class)))
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
    @GetMapping("/{id}")
    @Operation(summary = "모의고사 상세 조회", description = "특정 모의고사의 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 모의고사 정보를 반환함",
            content = @Content(schema = @Schema(implementation = ExamResponse.class)))
    public ResponseEntity<ExamResponse> getExam(@Parameter(description = "모의고사 ID") @PathVariable Long id) {
        Exam exam = examService.getExam(id);
        return ResponseEntity.ok(ExamResponse.of(exam));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "카테고리별 모의고사 조회", description = "특정 카테고리의 모의고사 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 카테고리별 모의고사 목록을 반환함",
            content = @Content(schema = @Schema(implementation = ExamResponse.class)))
    public ResponseEntity<List<ExamResponse>> getExamsByCategory(
            @Parameter(description = "카테고리 ID") @PathVariable Long categoryId) {
        List<Exam> exams = examService.getExamsByCategory(categoryId);
        List<ExamResponse> response = exams.stream()
                .map(ExamResponse::of)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "모의고사 수정", description = "특정 모의고사의 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 모의고사를 수정함")
    @ResourceOwner(idParameter = "id")
    public ResponseEntity<Void> updateExam(
            @Parameter(description = "모의고사 ID") @PathVariable Long id,
            @RequestBody ExamCreateRequest request,
            Principal principal) {

        String email = userService.extractEmail(principal);
        Long userId = userService.getUserByEmail(email).getId();
        examService.updateExam(id, request, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "모의고사 삭제", description = "특정 모의고사를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "성공적으로 모의고사를 삭제함")
    @ResourceOwner(idParameter = "id")
    public ResponseEntity<Void> deleteExam(
            @Parameter(description = "모의고사 ID") @PathVariable Long id,
            Principal principal) {
        try {
            String email = userService.extractEmail(principal);
            Long userId = userService.getUserByEmail(email).getId();
            examService.deleteExam(id, userId);
            return ResponseEntity.noContent().build();  // 204 No Content
        } catch (Exception e) {
            log.error("Failed to delete exam", e);
            throw new CustomException(ErrorCode.EXAM_DELETE_FAILED);
        }
    }

    @PostMapping("/{examId}/start")
    @Operation(summary = "시험 시작", description = "특정 모의고사의 시험을 시작합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 시험을 시작함",
            content = @Content(schema = @Schema(implementation = Long.class)))
    public ResponseEntity<Long> startExam(@Parameter(description = "모의고사 ID") @PathVariable Long examId, Principal principal) {
        String email = userService.extractEmail(principal);
        Long userId = userService.getUserByEmail(email).getId();

        ExamAttemptCreateDto createDto = new ExamAttemptCreateDto();
        createDto.setExamId(examId);

        ExamAttemptResponseDto attempt = examAttemptService.startExam(createDto, userId);
        return ResponseEntity.ok(attempt.getId());
    }

    @GetMapping("/{examId}/attempts/{attemptId}")
    @Operation(summary = "시험 응시 정보 조회", description = "특정 시험 응시의 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 시험 응시 정보를 반환함",
            content = @Content(schema = @Schema(implementation = ExamAttemptResponseDto.class)))
    public ResponseEntity<ExamAttemptResponseDto> getAttempt(
            @Parameter(description = "모의고사 ID") @PathVariable Long examId,
            @Parameter(description = "시험 응시 ID") @PathVariable Long attemptId,
            Principal principal) {
        String email = userService.extractEmail(principal);
        Long userId = userService.getUserByEmail(email).getId();

        ExamAttemptResponseDto attempt = examAttemptService.getExamAttempt(attemptId, userId);
        return ResponseEntity.ok(attempt);
    }

    @PostMapping("/{examId}/attempts/{attemptId}/save")
    @Operation(summary = "답안 임시 저장", description = "시험 중 답안을 임시 저장합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 답안을 임시 저장함")
    public ResponseEntity<Void> saveAnswers(
            @Parameter(description = "모의고사 ID") @PathVariable Long examId,
            @Parameter(description = "시험 응시 ID") @PathVariable Long attemptId,
            @RequestBody List<ExamAnswerSubmitDto> answers,
            Principal principal) {
        String email = userService.extractEmail(principal);
        Long userId = userService.getUserByEmail(email).getId();

        examAttemptService.saveAnswers(attemptId, answers, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{examId}/attempts/{attemptId}/submit")
    @Operation(summary = "답안 최종 제출", description = "시험 답안을 최종 제출합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 답안을 제출함",
            content = @Content(schema = @Schema(implementation = ExamAttemptResponseDto.class)))
    public ResponseEntity<ExamAttemptResponseDto> submitExam(
            @Parameter(description = "모의고사 ID") @PathVariable Long examId,
            @Parameter(description = "시험 응시 ID") @PathVariable Long attemptId,
            @RequestBody List<ExamAnswerSubmitDto> answers,
            Principal principal) {
        String email = userService.extractEmail(principal);
        Long userId = userService.getUserByEmail(email).getId();

        ExamAttemptResponseDto result = examAttemptService.submitExam(attemptId, answers, userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{examId}/attempts/{attemptId}/cancel")
    @Operation(summary = "시험 취소", description = "진행 중인 시험을 취소합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 시험을 취소함")
    public String cancelExam(@Parameter(description = "모의고사 ID") @PathVariable Long examId,
                             @Parameter(description = "시험 응시 ID") @PathVariable Long attemptId,
                             Principal principal) {
        String email = userService.extractEmail(principal);
        Long userId = userService.getUserByEmail(email).getId();

        examAttemptService.cancelExamAttempt(attemptId, userId);

        return "redirect:/exams";
    }
}