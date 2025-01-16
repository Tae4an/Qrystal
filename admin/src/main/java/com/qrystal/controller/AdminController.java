package com.qrystal.controller;

import com.qrystal.app.admin.service.AdminService;
import com.qrystal.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // 대시보드 (기본 페이지)
    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("admin", SecurityUtil.getCurrentAdmin());
        return "admin/index";
    }

    // 관리자 관리 페이지
    @GetMapping("/admins")
    public String adminManagement(Model model) {
        model.addAttribute("admin", SecurityUtil.getCurrentAdmin());
        model.addAttribute("content", "admins");
        model.addAttribute("adminList", adminService.getAllAdmins());
        return "admin/index";
    }

    // 사용자 관리 페이지
    @GetMapping("/users")
    public String userManagement(Model model) {
        model.addAttribute("admin", SecurityUtil.getCurrentAdmin());
        model.addAttribute("content", "users");
        return "admin/index";
    }
}