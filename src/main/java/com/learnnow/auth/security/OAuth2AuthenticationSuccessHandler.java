package com.learnnow.auth.security;

import com.learnnow.auth.jwt.JwtTokenProvider;
import com.learnnow.user.model.User;
import com.learnnow.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    @Value("${app.frontend-link}")
    private String frontLink;
    @Value("${app.backend-link}")
    private String backLink;

    public OAuth2AuthenticationSuccessHandler(JwtTokenProvider jwtTokenProvider, UserService userService){
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        User user = userService.processOAuthPostLogin(email, name);
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        Authentication manualAuthentication = new UsernamePasswordAuthenticationToken(
                userPrincipal,
                null,
                userPrincipal.getAuthorities()
        );

        String token = jwtTokenProvider.generateToken(manualAuthentication);
        String targetUrl = UriComponentsBuilder.fromUriString(frontLink +  "/oauth2/redirect")
                .queryParam("token", token)
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}