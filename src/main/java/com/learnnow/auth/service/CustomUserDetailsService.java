package com.learnnow.auth.service;


import com.learnnow.auth.security.UserPrincipal;
import com.learnnow.user.model.User;
import com.learnnow.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService { //implementation of userService needed for Spring Security

    @Autowired
    private UserRepository userRepository;

    /**
     * Locates the user based on the username. This is required by Spring Security
     * used during authentication (in the login process) and during token validation.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email : " + email));

        return UserPrincipal.create(user);
    }
}