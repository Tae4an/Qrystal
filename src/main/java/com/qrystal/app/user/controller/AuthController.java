package com.qrystal.app.user.controller;

import com.qrystal.app.user.dto.UserSignupRequest;
import com.qrystal.app.user.service.AuthService;
import com.qrystal.app.user.service.UserService;
import com.qrystal.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.validation.Valid;
@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("userSignupRequest", new UserSignupRequest());
        return "auth/signup";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("userSignupRequest") UserSignupRequest request,
                         BindingResult bindingResult,
                         RedirectAttributes attributes) {
        if (bindingResult.hasErrors()) {
            return "auth/signup";
        }

        userService.signup(request);
        attributes.addFlashAttribute("message", "회원가입이 완료되었습니다.");
        return "redirect:/auth/login";
    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam String token, RedirectAttributes attributes) {
        try {
            userService.verifyEmail(token);
            attributes.addFlashAttribute("message", "이메일 인증이 완료되었습니다. 로그인해주세요.");
            return "redirect:/auth/login";
        } catch (CustomException e) {
            attributes.addFlashAttribute("error", "유효하지 않은 인증 링크입니다.");
            return "redirect:/auth/login";
        }
    }



    @GetMapping("/login")
    public String loginForm() {
        return "auth/login";
    }
}