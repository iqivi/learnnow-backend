package com.learnnow.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService; // A service to load user details

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            // 1. Get JWT from the request
            String jwt = getJwtFromRequest(request);

            // 2. Validate token and load user if token is valid
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {

                // Get username (subject) from token
                String username = tokenProvider.getUsernameFromJWT(jwt);

                // Load user data from the database
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                // Create an authentication object
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                // Set authentication details (like IP address, session ID, etc.)
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 3. Set the Authentication in Spring Security's context
                // This is what tells Spring Security the user is authenticated for this request.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // Log authentication failure
            logger.error("Could not set user authentication in security context", ex);
        }

        // 4. Continue the filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT from the "Authorization: Bearer <token>" header.
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        // Check if the header starts with "Bearer " and extract the token
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}