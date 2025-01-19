package com.qrystal.controller;

import com.qrystal.app.user.dto.UserResponse;
import com.qrystal.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {
    private final UserService userService;

    private void addUserToModel(Model model, Object principal) {
        if (principal == null) {
            return;
        }

        try {
            String email = userService.extractEmail(principal);
            UserResponse user = userService.findByEmail(email);
            model.addAttribute("user", user);
        } catch (Exception e) {
            log.debug("User info not loaded (user might not be logged in)");
        }
    }

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal Object principal) {
        addUserToModel(model, principal);
        model.addAttribute("content", "home");
        return "index";
    }
}