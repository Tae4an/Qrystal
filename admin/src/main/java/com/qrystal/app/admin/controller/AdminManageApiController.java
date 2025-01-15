package com.qrystal.app.admin.controller;

import com.qrystal.app.admin.dto.AdminCreateRequest;
import com.qrystal.app.admin.dto.AdminResponse;
import com.qrystal.app.admin.dto.AdminStatusUpdateRequest;
import com.qrystal.app.admin.dto.AdminUpdateRequest;
import com.qrystal.app.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/admin/admins")
@RequiredArgsConstructor
public class AdminManageApiController {
    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<List<AdminResponse>> getAdminList() {
        try {
            List<AdminResponse> admins = adminService.getAllAdmins();
            return ResponseEntity.ok(admins);
        } catch (Exception e) {
            log.error("관리자 목록 조회 실패", e);
            throw e;
        }
    }

    @PostMapping
    public ResponseEntity<Void> createAdmin(@Valid @RequestBody AdminCreateRequest request) {
        try {
            adminService.createAdmin(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("관리자 생성 실패", e);
            throw e;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateAdmin(
            @PathVariable Long id,
            @Valid @RequestBody AdminUpdateRequest request) {
        try {
            adminService.updateAdmin(id, request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("관리자 수정 실패", e);
            throw e;
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Void> updateAdminStatus(
            @PathVariable Long id,
            @RequestBody AdminStatusUpdateRequest request) {
        try {
            adminService.updateAdminStatus(id, request.getStatus());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("관리자 상태 변경 실패", e);
            throw e;
        }
    }
}