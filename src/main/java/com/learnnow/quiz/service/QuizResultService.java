package com.learnnow.quiz.service;

import com.learnnow.quiz.repository.QuizResultRepository;
import org.springframework.stereotype.Service;

@Service
public class QuizResultService {
    private final QuizResultRepository quizResultRepository;

    public QuizResultService(QuizResultRepository quizResultRepository) {
        this.quizResultRepository = quizResultRepository;
    }
}
