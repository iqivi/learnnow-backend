package com.learnnow.auth.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        // Log the reason for the failure
        logger.error("Unauthorized error: {}", authException.getMessage());

        // 1. Set the response status to 401 Unauthorized
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                "Access Denied: You must authenticate with a valid JWT token to access this resource.");

    }
}