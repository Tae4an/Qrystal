package com.qrystal.app.user.controller;

import com.qrystal.app.exam.dto.ExamAttemptResponseDto;
import com.qrystal.app.exam.model.Exam;
import com.qrystal.app.exam.service.ExamAttemptService;
import com.qrystal.app.exam.service.ExamService;
import com.qrystal.app.question.model.Question;
import com.qrystal.app.question.service.QuestionService;
import com.qrystal.app.user.dto.*;
import com.qrystal.app.user.service.UserService;
import com.qrystal.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.qrystal.exception.ErrorCode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final QuestionService questionService;
    private final ExamService examService;
    private final ExamAttemptService examAttemptService;

    @GetMapping("/profile")
    public String profile(Model model, @org.springframework.security.web.bind.annotation.AuthenticationPrincipal Object principal) {
        try {
            String email = userService.extractEmail(principal);
            UserResponse user = userService.findByEmail(email);
            model.addAttribute("user", user);
            model.addAttribute("content", "user/profile");
            return "index";
        } catch (Exception e) {
            log.error("Failed to load profile: {}", e.getMessage());
            return "redirect:/auth/login";
        }
    }
    @GetMapping("/profile/{contentType}")
    public String getProfileContent(@PathVariable String contentType,
                                    @AuthenticationPrincipal Object principal,
                                    Model model) {
        try {
            String email = userService.extractEmail(principal);
            UserResponse user = userService.findByEmail(email);
            model.addAttribute("user", user);

            switch (contentType) {
                case "my-questions":
                    List<Question> questions = questionService.getMyQuestions(user.getId());
                    log.debug("Loaded questions size: {}", questions.size());
                    model.addAttribute("questions", questions);
                    return "question/my-questions :: content";

                case "my-exams":
                    List<Exam> exams = examService.getMyExams(user.getId());
                    model.addAttribute("exams", exams);
                    return "exam/my-exams :: content";

                case "exam-results":
                    List<ExamAttemptResponseDto> attempts = examAttemptService.getMyAttempts(user.getId());
                    model.addAttribute("attempts", attempts);
                    return "exam/results :: content";

                case "edit-profile":
                    return "user/edit :: content";

                case "change-password":
                    return "user/password :: content";

                case "statistics":
                    List<ExamAttemptResponseDto> examAttempts = examAttemptService.getMyAttempts(user.getId());

                    if (examAttempts.isEmpty()) {
                        model.addAttribute("noData", true);
                        return "user/statistics :: content";
                    }

                    // 1. 기본 통계 계산
                    double averageScore = examAttempts.stream()
                            .mapToInt(ExamAttemptResponseDto::getTotalScore)
                            .average()
                            .orElse(0.0);

                    double averageCorrectRate = examAttempts.stream()
                            .mapToDouble(ExamAttemptResponseDto::getCorrectRate)
                            .average()
                            .orElse(0.0);

                    // 2. 월별 통계 데이터 계산
                    Map<String, List<ExamAttemptResponseDto>> monthlyGroups = examAttempts.stream()
                            .collect(Collectors.groupingBy(a ->
                                    a.getSubmittedAt().format(DateTimeFormatter.ofPattern("yyyy-MM"))));

                    List<MonthlyStatDto> monthlyData = monthlyGroups.entrySet().stream()
                            .map(entry -> {
                                List<ExamAttemptResponseDto> monthAttempts = entry.getValue();
                                return new MonthlyStatDto(
                                        entry.getKey(),
                                        monthAttempts.stream()
                                                .mapToInt(ExamAttemptResponseDto::getTotalScore)
                                                .average()
                                                .orElse(0.0),
                                        monthAttempts.size(),
                                        monthAttempts.stream()
                                                .mapToDouble(ExamAttemptResponseDto::getCorrectRate)
                                                .average()
                                                .orElse(0.0),
                                        monthAttempts.stream()
                                                .mapToInt(ExamAttemptResponseDto::getTotalScore)
                                                .max()
                                                .orElse(0),
                                        monthAttempts.stream()
                                                .mapToInt(ExamAttemptResponseDto::getTotalScore)
                                                .min()
                                                .orElse(0)
                                );
                            })
                            .sorted(Comparator.comparing(MonthlyStatDto::getMonth))
                            .collect(Collectors.toList());

                    // 3. 카테고리별 통계 데이터 계산
                    Map<String, List<ExamAttemptResponseDto>> categoryGroups = examAttempts.stream()
                            .collect(Collectors.groupingBy(ExamAttemptResponseDto::getCategoryName));

                    List<CategoryStatDto> categoryData = categoryGroups.entrySet().stream()
                            .map(entry -> new CategoryStatDto(
                                    entry.getKey(),
                                    entry.getValue().stream()
                                            .mapToInt(ExamAttemptResponseDto::getTotalScore)
                                            .average()
                                            .orElse(0.0),
                                    entry.getValue().size()
                            ))
                            .collect(Collectors.toList());

                    // 4. 정답률 분포 데이터 계산
                    Map<String, Long> correctRateGroups = examAttempts.stream()
                            .collect(Collectors.groupingBy(
                                    examAttempt -> {
                                        int range = (int) (examAttempt.getCorrectRate() / 10) * 10;
                                        return range + "-" + (range + 10) + "%";
                                    },
                                    Collectors.counting()
                            ));

                    List<CorrectRateStatDto> correctRateData = correctRateGroups.entrySet().stream()
                            .map(entry -> new CorrectRateStatDto(
                                    entry.getKey(),
                                    entry.getValue().intValue()
                            ))
                            .sorted(Comparator.comparing(CorrectRateStatDto::getRange))
                            .collect(Collectors.toList());

                    // 모델에 데이터 추가
                    model.addAttribute("attempts", examAttempts);
                    model.addAttribute("averageScore", averageScore);
                    model.addAttribute("averageCorrectRate", averageCorrectRate);
                    model.addAttribute("monthlyData", monthlyData);
                    model.addAttribute("categoryData", categoryData);
                    model.addAttribute("correctRateData", correctRateData);

                    return "user/statistics :: content";

                default:
                    // 프로필 기본 정보
                    model.addAttribute("content", "user/profile");
                    return "index";
            }

        } catch (Exception e) {
            log.error("Failed to load profile content: {}", e.getMessage());
            if (RequestUtil.isAjaxRequest()) {
                // AJAX 요청인 경우 에러 응답
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
            }
            return "redirect:/auth/login";
        }
    }

    // AJAX 요청 확인용 유틸리티 클래스
    @UtilityClass
    private static class RequestUtil {
        public static boolean isAjaxRequest() {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes instanceof ServletRequestAttributes) {
                HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
                String headerValue = request.getHeader("X-Requested-With");
                return "XMLHttpRequest".equals(headerValue);
            }
            return false;
        }
    }

    // 시험 결과 상세
    @GetMapping("/exam-results/{attemptId}")
    public String examResultDetail(@PathVariable Long attemptId,
                                   @AuthenticationPrincipal Object principal,
                                   Model model) {
        try {
            String email = userService.extractEmail(principal);
            UserResponse user = userService.findByEmail(email);

            ExamAttemptResponseDto attempt = examAttemptService.getExamResult(attemptId);

            // 권한 체크
            if (!attempt.getUserId().equals(user.getId())) {
                throw new CustomException(ErrorCode.ACCESS_DENIED);
            }

            model.addAttribute("attempt", attempt);
            model.addAttribute("content", "exam/result");
            return "index";
        } catch (Exception e) {
            log.error("Failed to load exam result: {}", e.getMessage());
            return "redirect:/user/profile/exam-results";
        }
    }

    @GetMapping("/edit")
    public String editForm(Model model, @org.springframework.security.web.bind.annotation.AuthenticationPrincipal Object principal) {
        try {
            String email = userService.extractEmail(principal);
            UserResponse user = userService.findByEmail(email);
            model.addAttribute("user", user);
            model.addAttribute("content", "user/edit");
            return "index";
        } catch (Exception e) {
            log.error("Failed to load edit profile page: {}", e.getMessage());
            return "redirect:/auth/login";
        }
    }

    @GetMapping("/password")
    public String passwordForm(Model model, @org.springframework.security.web.bind.annotation.AuthenticationPrincipal Object principal) {
        try {
            String email = userService.extractEmail(principal);
            UserResponse user = userService.findByEmail(email);
            model.addAttribute("user", user);
            model.addAttribute("content", "user/password");
            return "index";
        } catch (Exception e) {
            log.error("Failed to load password change page: {}", e.getMessage());
            return "redirect:/auth/login";
        }
    }

    @PostMapping("/edit")
    public String updateProfile(@AuthenticationPrincipal Object principal,
                                @Valid UserUpdateRequest request,
                                RedirectAttributes attributes) {
        try {
            String email = userService.extractEmail(principal);
            userService.updateProfile(email, request);
            attributes.addFlashAttribute("message", "프로필이 성공적으로 수정되었습니다.");
            return "redirect:/user/profile";
        } catch (Exception e) {
            log.error("프로필 수정 실패", e);
            attributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/edit";
        }
    }

    @PostMapping("/password")
    public String updatePassword(@AuthenticationPrincipal Object principal,
                                 @Valid PasswordUpdateRequest request,
                                 RedirectAttributes attributes) {
        try {
            String email = userService.extractEmail(principal);
            userService.updatePassword(email, request);
            attributes.addFlashAttribute("message", "비밀번호가 성공적으로 변경되었습니다.");
            return "redirect:/user/profile";
        } catch (Exception e) {
            log.error("비밀번호 변경 실패", e);
            attributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/password";
        }
    }

    @PostMapping("/deactivate")
    public String deactivateAccount(@AuthenticationPrincipal Object principal,
                                    @RequestParam(required = false) String password,
                                    HttpServletRequest request,
                                    RedirectAttributes attributes) {
        try {
            String email = userService.extractEmail(principal);
            userService.deactivateAccount(email, password);

            // 로그아웃 처리
            SecurityContextHolder.clearContext();
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }

            attributes.addFlashAttribute("message", "회원 탈퇴가 완료되었습니다.");
            return "redirect:/";
        } catch (Exception e) {
            log.error("계정 비활성화 실패", e);
            attributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/user/profile";
        }
    }
}