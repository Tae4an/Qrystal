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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
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
                         HttpServletRequest servletRequest) {
        if (bindingResult.hasErrors()) {
            return "auth/signup";
        }
        userService.signup(request, servletRequest);
        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "auth/login";
    }
}