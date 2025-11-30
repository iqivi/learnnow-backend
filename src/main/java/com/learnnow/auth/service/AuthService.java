package com.learnnow.auth.service;

import com.learnnow.user.exception.UserNotFoundException;
import com.learnnow.user.model.User;
import com.learnnow.user.repository.UserRepository;
import com.learnnow.user.service.UserService;

import java.util.Optional;

public class AuthService {
    private final UserRepository userRepository;
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty())
            throw new UserNotFoundException();
        return user.get();
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }


}
