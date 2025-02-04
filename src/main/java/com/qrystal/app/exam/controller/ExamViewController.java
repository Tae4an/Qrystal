package com.qrystal.app.exam.controller;

import com.qrystal.app.exam.service.ExamAttemptService;
import com.qrystal.app.exam.service.ExamService;
import com.qrystal.app.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/exams")
@RequiredArgsConstructor
public class ExamViewController {
    private final ExamService examService;
    private final UserService userService;
    private final ExamAttemptService examAttemptService;
    @GetMapping("")
    public String examList(Model model) {
        model.addAttribute("content", "exam/list");  // 모의고사 목록 페이지
        return "index";
    }
    
    @GetMapping("/my")
    public String myExams(Model model) {
        model.addAttribute("content", "exam/my-exams");  // 내 모의고사 목록
        return "index";
    }
    
    @GetMapping("/new")
    public String examForm(Model model) {
        model.addAttribute("content", "exam/form");  // 모의고사 생성 폼
        return "index";
    }
    
    @GetMapping("/{id}/edit")
    public String examEdit(@PathVariable Long id, Model model) {
        model.addAttribute("content", "exam/form");
        model.addAttribute("examId", id);
        model.addAttribute("exam", examService.getExam(id));
        return "index";
    }
    @GetMapping("/{id}")
    public String examDetail(@PathVariable Long id, Model model) {
        model.addAttribute("content", "exam/detail");
        model.addAttribute("exam", examService.getExam(id));
        return "index";
    }

    @GetMapping("/{id}/attempt")
    public String examAttempt(@PathVariable Long id,
                              @RequestParam(required = false) Long attemptId,
                              Model model) {
        model.addAttribute("content", "exam/attempt");
        model.addAttribute("exam", examService.getExam(id));
        if (attemptId != null) {
            model.addAttribute("attempt", examAttemptService.getExamResult(attemptId));
        }
        return "index";
    }

    @GetMapping("/{id}/result/{attemptId}")
    public String examResult(@PathVariable Long id,
                             @PathVariable Long attemptId,
                             Model model) {
        model.addAttribute("content", "exam/result");
        model.addAttribute("exam", examService.getExam(id));
        model.addAttribute("attempt", examAttemptService.getExamResult(attemptId));
        return "index";
    }
}