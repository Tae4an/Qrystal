package com.qrystal.controller;

import com.qrystal.app.admin.service.AdminService;
import com.qrystal.app.user.domain.UserStatus;
import com.qrystal.app.user.dto.UserResponse;
import com.qrystal.app.user.dto.UserSearchCondition;
import com.qrystal.app.user.service.UserService;
import com.qrystal.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
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
    public String userManagement(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) String type,
            Model model) {

        model.addAttribute("admin", SecurityUtil.getCurrentAdmin());

        UserSearchCondition condition = UserSearchCondition.builder()
                .search(search)
                .status(status)
                .type(type)
                .build();

        Page<UserResponse> users = userService.getUsers(condition, PageRequest.of(page, size));

        model.addAttribute("users", users);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("type", type);
        model.addAttribute("content", "users");

        return "admin/index";
    }

    @GetMapping("/category")
    public String categoryList(Model model) {
        model.addAttribute("admin", SecurityUtil.getCurrentAdmin());
        model.addAttribute("content", "category/list");
        return "admin/index";
    }
}