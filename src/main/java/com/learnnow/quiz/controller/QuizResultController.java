package com.learnnow.quiz.controller;

import com.learnnow.quiz.service.QuizResultService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quiz/result")
public class QuizResultController {
    private QuizResultService quizResultService;

    public QuizResultController(QuizResultService quizResultService) {
        this.quizResultService = quizResultService;
    }
}
