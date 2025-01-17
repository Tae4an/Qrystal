package com.qrystal.app.user.controller;

import com.qrystal.app.user.domain.UserStatus;
import com.qrystal.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserManageApiController {
    private final UserService userService;

    @PostMapping("/{id}/status")
    public ResponseEntity<Void> updateUserStatus(
            @PathVariable Long id,
            @RequestParam UserStatus status) {
        try {
            userService.updateUserStatus(id, status);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("사용자 상태 변경 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}