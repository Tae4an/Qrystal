package com.qrystal.app.user.controller;

import com.qrystal.app.user.dto.PasswordUpdateRequest;
import com.qrystal.app.user.dto.UserResponse;
import com.qrystal.app.user.dto.UserUpdateRequest;
import com.qrystal.app.user.service.UserService;
import com.qrystal.exception.CustomException;
import com.qrystal.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public String profile(Model model, @AuthenticationPrincipal Object principal) {
        try {
            // 사용자 이메일 추출
            String email;

            // 일반 로그인 사용자 처리
            if (principal instanceof UserDetails) {
                email = ((UserDetails) principal).getUsername();
            }
            // OAuth2 로그인 사용자 처리
            else if (principal instanceof DefaultOAuth2User) {
                Map<String, Object> attributes = ((DefaultOAuth2User) principal).getAttributes();

                // 구글/깃허브: 기본 email 필드
                if (attributes.containsKey("email")) {
                    email = (String) attributes.get("email");
                }
                // 네이버: response 객체 내부에 email 존재
                else if (attributes.containsKey("response")) {
                    Map<String, Object> response = (Map<String, Object>) attributes.get("response");
                    email = (String) response.get("email");
                }
                // 카카오: kakao_account 객체 내부에 email 존재
                else {
                    Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                    email = (String) kakaoAccount.get("email");
                }
            }
            // 인증 정보가 없는 경우
            else {
                throw new CustomException(ErrorCode.USER_NOT_FOUND);
            }

            log.info("프로필 조회 요청 - email: {}", email);
            UserResponse user = userService.findByEmail(email);
            model.addAttribute("user", user);
            return "user/profile";

        } catch (Exception e) {
            log.error("프로필 조회 실패", e);
            throw e;
        }
    }

    @GetMapping("/edit")
    public String editForm(Model model, @AuthenticationPrincipal Object principal) {
        try {
            String email = userService.extractEmail(principal);
            UserResponse user = userService.findByEmail(email);
            model.addAttribute("user", user);
            return "user/edit";
        } catch (Exception e) {
            log.error("프로필 수정 페이지 로드 실패", e);
            throw e;
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

    // 비밀번호 변경 페이지
    @GetMapping("/password")
    public String passwordForm() {
        return "user/password";
    }

    // 비밀번호 변경 처리
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

    // 회원 탈퇴
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