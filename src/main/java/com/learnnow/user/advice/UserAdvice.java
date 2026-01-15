package com.learnnow.user.advice;

import com.learnnow.user.exception.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserAdvice {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Void> userNotFound(UserNotFoundException e) {
        return ResponseEntity.notFound().build();
    }
}
