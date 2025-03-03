package com.qrystal.app.question.controller;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import com.qrystal.app.question.domain.QuestionStatus;
import com.qrystal.app.question.dto.QuestionCreateRequest;
import com.qrystal.app.question.dto.QuestionResponse;
import com.qrystal.app.question.dto.QuestionSearchCondition;
import com.qrystal.app.question.dto.QuestionUpdateRequest;
import com.qrystal.global.annotation.ResourceOwner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.qrystal.app.question.model.Question;
import com.qrystal.app.question.model.QuestionChoice;
import com.qrystal.app.question.service.QuestionService;
import com.qrystal.app.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;


@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "문제 관리", description = "문제 관련 API")
public class QuestionController {

    private final QuestionService questionService;
    private final UserService userService;

    @GetMapping
    @Operation(summary = "문제 목록 조회", description = "조건에 맞는 문제 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 문제 목록을 반환함",
            content = @Content(schema = @Schema(implementation = QuestionResponse.class)))
    public ResponseEntity<List<QuestionResponse>> getQuestions(
            @Parameter(description = "문제 검색 조건") @ModelAttribute QuestionSearchCondition condition) {
        List<Question> questions = questionService.getQuestions(condition);
        List<QuestionResponse> response = questions.stream()
                .map(QuestionResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "문제 상세 조회", description = "특정 문제의 상세 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 문제 정보를 반환함",
            content = @Content(schema = @Schema(implementation = QuestionResponse.class)))
    public ResponseEntity<QuestionResponse> getQuestion(
            @Parameter(description = "문제 ID") @PathVariable Long id) {
        Question question = questionService.getQuestion(id);
        return ResponseEntity.ok(QuestionResponse.from(question));
    }

    @PostMapping
    @Operation(summary = "문제 생성", description = "새로운 문제를 생성합니다.")
    @ApiResponse(responseCode = "201", description = "성공적으로 문제를 생성함",
            content = @Content(schema = @Schema(implementation = QuestionResponse.class)))
    public ResponseEntity<QuestionResponse> createQuestion(
            @RequestBody @Valid QuestionCreateRequest request,
            Principal principal) {
        String email = userService.extractEmail(principal);
        Long userId = userService.getUserByEmail(email).getId();

        Question question = Question.builder()
                .categoryId(request.getCategoryId())
                .userId(userId)
                .typeId(request.getTypeId())
                .title(request.getTitle())
                .content(request.getContent())
                .answer(request.getAnswer())
                .explanation(request.getExplanation())
                .difficulty(request.getDifficulty())
                .isPublic(request.getIsPublic())
                .choices(request.getChoices() != null ?
                        request.getChoices().stream()
                                .map(choice -> QuestionChoice.builder()
                                        .choiceNumber(choice.getChoiceNumber())
                                        .content(choice.getContent())
                                        .build())
                                .collect(Collectors.toList()) : null)
                .tags(request.getTags())
                .build();

        Question created = questionService.createQuestion(question);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(QuestionResponse.from(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "문제 수정", description = "특정 문제의 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 문제를 수정함",
            content = @Content(schema = @Schema(implementation = QuestionResponse.class)))
    @ResourceOwner(idParameter = "id")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @Parameter(description = "문제 ID") @PathVariable Long id,
            @RequestBody @Valid QuestionUpdateRequest request,
            Principal principal) {
        Question question = Question.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .answer(request.getAnswer())
                .explanation(request.getExplanation())
                .difficulty(request.getDifficulty())
                .isPublic(request.getIsPublic())
                .choices(request.getChoices() != null ?
                        request.getChoices().stream()
                                .map(choice -> QuestionChoice.builder()
                                        .choiceNumber(choice.getChoiceNumber())
                                        .content(choice.getContent())
                                        .build())
                                .collect(Collectors.toList()) : null)
                .tags(request.getTags())
                .build();

        Question updated = questionService.updateQuestion(id, question);
        return ResponseEntity.ok(QuestionResponse.from(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "문제 삭제", description = "특정 문제를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "성공적으로 문제를 삭제함")
    @ResourceOwner(idParameter = "id")
    public ResponseEntity<Void> deleteQuestion(
            @Parameter(description = "문제 ID") @PathVariable Long id,
            Principal principal) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    @Operation(summary = "내 문제 목록 조회", description = "사용자의 문제 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 내 문제 목록을 반환함",
            content = @Content(schema = @Schema(implementation = QuestionResponse.class)))
    public ResponseEntity<List<QuestionResponse>> getMyQuestions(Principal principal) {
        try {
            log.debug("Principal in getMyQuestions: {}", principal);
            String email = userService.extractEmail(principal);
            Long userId = userService.getUserByEmail(email).getId();

            List<Question> questions = questionService.getMyQuestions(userId);
            List<QuestionResponse> response = questions.stream()
                    .map(QuestionResponse::from)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("내 문제 목록 조회 실패", e);
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "카테고리별 문제 목록 조회", description = "특정 카테고리의 문제 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 카테고리별 문제 목록을 반환함",
            content = @Content(schema = @Schema(implementation = QuestionResponse.class)))
    public ResponseEntity<List<QuestionResponse>> getQuestionsByCategory(
            @Parameter(description = "카테고리 ID") @PathVariable Long categoryId) {
        List<Question> questions = questionService.getQuestionsByCategory(categoryId);
        List<QuestionResponse> response = questions.stream()
                .map(QuestionResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "문제 상태 업데이트", description = "특정 문제의 상태를 업데이트합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 문제 상태를 업데이트함",
            content = @Content(schema = @Schema(implementation = QuestionResponse.class)))
    @ResourceOwner(idParameter = "id")
    public ResponseEntity<QuestionResponse> updateQuestionStatus(
            @Parameter(description = "문제 ID") @PathVariable Long id,
            @Parameter(description = "문제 상태") @RequestParam QuestionStatus status,
            Principal principal) {
        questionService.updateQuestionStatus(id, status);
        Question updated = questionService.getQuestion(id);
        return ResponseEntity.ok(QuestionResponse.from(updated));
    }
}