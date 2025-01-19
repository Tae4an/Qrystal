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
    private final UserService userService;
    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("content", "auth/login");
        return "index";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("content", "auth/signup");
        model.addAttribute("userSignupRequest", new UserSignupRequest());
        return "index";
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("userSignupRequest") UserSignupRequest request,
                         BindingResult bindingResult,
                         RedirectAttributes attributes) {
        if (bindingResult.hasErrors()) {
            return "redirect:/auth/signup";
        }

        userService.signup(request);
        attributes.addFlashAttribute("message", "회원가입이 완료되었습니다.");
        return "redirect:/auth/login";
    }
}