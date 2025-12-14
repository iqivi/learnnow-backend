package com.learnnow.quiz.service;

import com.learnnow.quiz.repository.QuizRepository;
import org.springframework.stereotype.Service;

@Service
public class QuizService {
    private final QuizRepository quizRepository;

    public QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }
}
