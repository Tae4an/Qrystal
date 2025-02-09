package com.qrystal.app.question.controller;

import com.qrystal.global.annotation.ResourceOwner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/questions")
@RequiredArgsConstructor
public class QuestionViewController {

    @GetMapping("")
    public String questionList(Model model) {
        model.addAttribute("content", "question/list");
        return "index";  // 메인 레이아웃 템플릿
    }

    @GetMapping("/my")
    public String myQuestions(Model model) {
        model.addAttribute("content", "question/my-questions");
        return "index";
    }

    @GetMapping("/new")
    public String questionForm(Model model) {
        model.addAttribute("content", "question/form");
        return "index";
    }

    @GetMapping("/{id}/edit")
    @ResourceOwner(idParameter = "id")
    public String questionEdit(@PathVariable Long id, Model model) {
        model.addAttribute("content", "question/form");
        model.addAttribute("questionId", id);
        return "index";
    }
}