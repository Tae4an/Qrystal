package com.qrystal.app.user.controller;

import com.qrystal.app.user.dto.UserSignupRequest;
import com.qrystal.app.user.service.EmailVerificationService;
import com.qrystal.app.user.service.UserService;
import com.qrystal.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class EmailVerificationController {
    private final EmailVerificationService emailVerificationService;
    private final UserService userService;

    @PostMapping("/send-verification")
    public ResponseEntity<Map<String, Object>> sendVerificationEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        
        try {
            emailVerificationService.sendVerificationCode(email);
            return ResponseEntity.ok(Map.of("success", true));
        } catch (CustomException e) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, Object>> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        
        try {
            boolean isValid = emailVerificationService.verifyCode(email, code);
            return ResponseEntity.ok(Map.of("success", isValid));
        } catch (CustomException e) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmailDuplicate(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        boolean isAvailable = userService.isEmailAvailable(email);
        if (isAvailable) {
            return ResponseEntity.ok(Map.of("success", true));
        } else {
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "이미 사용중인 이메일입니다."
            ));
        }
    }
}