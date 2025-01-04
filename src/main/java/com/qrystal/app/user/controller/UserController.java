package com.qrystal.app.user.controller;

import com.qrystal.app.user.dto.UserResponse;
import com.qrystal.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public String profile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        UserResponse user = userService.findByEmail(userDetails.getUsername());  // findById -> findByEmail
        model.addAttribute("user", user);
        return "user/profile";
    }
}