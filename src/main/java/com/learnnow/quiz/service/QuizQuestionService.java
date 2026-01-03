package com.learnnow.quiz.service;

import com.learnnow.quiz.repository.QuizQuestionRepository;
import org.springframework.stereotype.Service;

@Service
public class QuizQuestionService {
    private final QuizQuestionRepository quizQuestionRepository;

    public QuizQuestionService(QuizQuestionRepository quizQuestionRepository) {
        this.quizQuestionRepository = quizQuestionRepository;
    }
}
