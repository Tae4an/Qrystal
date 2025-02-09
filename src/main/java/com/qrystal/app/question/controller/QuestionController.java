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

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Slf4j
public class QuestionController {

    private final QuestionService questionService;
    private final UserService userService;

    // 문제 목록 조회
    @GetMapping
    public ResponseEntity<List<QuestionResponse>> getQuestions(
            @ModelAttribute QuestionSearchCondition condition) {
        List<Question> questions = questionService.getQuestions(condition);
        List<QuestionResponse> response = questions.stream()
                .map(QuestionResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // 문제 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> getQuestion(@PathVariable Long id) {
        Question question = questionService.getQuestion(id);
        return ResponseEntity.ok(QuestionResponse.from(question));
    }

    // 문제 생성
    @PostMapping
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

    // 문제 수정
    @PutMapping("/{id}")
    @ResourceOwner(idParameter = "id")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable Long id,
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

    // 문제 삭제
    @DeleteMapping("/{id}")
    @ResourceOwner(idParameter = "id")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long id,
            Principal principal) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    // 내 문제 목록
    @GetMapping("/my")
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

    // 카테고리별 문제 목록
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<QuestionResponse>> getQuestionsByCategory(
            @PathVariable Long categoryId) {
        List<Question> questions = questionService.getQuestionsByCategory(categoryId);
        List<QuestionResponse> response = questions.stream()
                .map(QuestionResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    @ResourceOwner(idParameter = "id")
    public ResponseEntity<QuestionResponse> updateQuestionStatus(
            @PathVariable Long id,
            @RequestParam QuestionStatus status,
            Principal principal) {
        questionService.updateQuestionStatus(id, status);
        Question updated = questionService.getQuestion(id);
        return ResponseEntity.ok(QuestionResponse.from(updated));
    }
}