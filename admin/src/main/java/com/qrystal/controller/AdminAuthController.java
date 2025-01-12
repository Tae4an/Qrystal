package com.qrystal.controller;

import com.qrystal.app.admin.security.AdminDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AdminAuthController {

   @GetMapping("/login")
   public String loginForm(@RequestParam(value = "error", required = false) Boolean error,
                         @RequestParam(value = "message", required = false) String message,
                         Model model) {
       if (error != null && error) {
           model.addAttribute("error", message != null ? message : "로그인에 실패했습니다.");
       }
       return "admin/auth/login";
   }

    @GetMapping("/")
    public String index(@AuthenticationPrincipal AdminDetails adminDetails, Model model) {
        model.addAttribute("admin", adminDetails.getAdmin());
        return "admin/index";
    }
}