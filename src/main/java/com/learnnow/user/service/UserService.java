package com.learnnow.user.service;

import com.learnnow.user.exception.UserNotFoundException;
import com.learnnow.user.model.User;
import com.learnnow.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty())
            throw new UserNotFoundException();
        return user.get();
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }
}
