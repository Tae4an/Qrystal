package com.qrystal.app.user.controller;

import com.qrystal.app.user.dto.PasswordUpdateRequest;
import com.qrystal.app.user.dto.UserResponse;
import com.qrystal.app.user.dto.UserUpdateRequest;
import com.qrystal.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
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

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;


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