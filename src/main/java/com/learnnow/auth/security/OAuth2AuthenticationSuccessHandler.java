package com.learnnow.auth.security;

import com.learnnow.auth.jwt.JwtTokenProvider;
import com.learnnow.user.model.User;
import com.learnnow.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.annotations.Comment;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public OAuth2AuthenticationSuccessHandler(JwtTokenProvider jwtTokenProvider, UserService userService){
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }
    // You'll need to inject your TokenProvider and UserService here
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // 1. Process user in Database
        User user = userService.processOAuthPostLogin(email, name);

        // 2. Generate our internal JWT for the frontend
        // We use the email/username and roles from our DB user
        String token = jwtTokenProvider.generateToken(user.getUsername());

        // 3. Redirect back to frontend with token
        // In a real app, you'd use a more secure way to pass the token,
        // but a URL param is standard for local dev/simplicity.
        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/oauth2/redirect")
                .queryParam("token", token)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}