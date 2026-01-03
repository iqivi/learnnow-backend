package com.learnnow.quiz.service;

import com.learnnow.quiz.repository.QuizAnswerRepository;
import org.springframework.stereotype.Service;

@Service
public class QuizAnswerService {
    private final QuizAnswerRepository quizAnswerRepository;

    public QuizAnswerService(QuizAnswerRepository quizAnswerRepository) {
        this.quizAnswerRepository = quizAnswerRepository;
    }
}
