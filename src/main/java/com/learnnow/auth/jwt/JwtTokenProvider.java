package com.learnnow.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs}")
    private long jwtExpirationInMs;

    /**
     * Helper method to get the signing key (SecretKey).
     */
    private SecretKey getSigningKey() {
        // NOTE: The secret key must be long enough for the chosen algorithm (e.g., 256 bits for HS256)
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a JWT token for the authenticated user.
     *
     * @param authentication The Spring Security Authentication object.
     * @return The generated JWT as a String.
     */
    public String generateToken(Authentication authentication) {

        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates the JWT token.
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey()) // CORRECTED: Use .verifyWith() for validation
                    .build()
                    .parseSignedClaims(authToken); // CORRECTED: Use .parseSignedClaims()
            return true;
        } catch (SignatureException ex) {
            // Log this: Invalid JWT signature
        } catch (MalformedJwtException ex) {
            // Log this: Invalid JWT token
        } catch (ExpiredJwtException ex) {
            // Log this: Expired JWT token
        } catch (UnsupportedJwtException ex) {
            // Log this: Unsupported JWT token
        } catch (IllegalArgumentException ex) {
            // Log this: JWT claims string is empty
        }
        return false;
    }

    /**
     * Gets the Username(in our case email) (subject) from the JWT token.
     */
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token) // CORRECTED: Use .parseSignedClaims()
                .getPayload(); // Get the claims body

        return claims.getSubject();
    }
}