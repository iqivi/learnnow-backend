package com.learnnow.usermanagement.service;

import com.learnnow.auth.jwt.JwtTokenProvider;
import com.learnnow.user.model.User;
import com.learnnow.user.model.UserRole;
import com.learnnow.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManagementService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public ManagementService(UserRepository userRepository, AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    public List<User> getAllUsers(String jwt) {
        if(tokenProvider.validateToken(jwt)){
            User user = userRepository.findByEmail(tokenProvider.getUsernameFromJWT(jwt)).get();
            if(user.getRole() == UserRole.ADMIN){
                return userRepository.findAll();
            }else{

            }
        }

    }

    public User getUserById(String jwt, long id) {
        return userRepository.findById(id).get();
    }

}
