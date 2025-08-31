package com.bookreview.auth;

import com.bookreview.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service class for JWT operations.
 */
@Service
@Slf4j
public class JwtService {

    @Value("${spring.security.jwt.secret}")
    private String secret;

    @Value("${spring.security.jwt.expiration}")
    private long accessTokenExpiration;

    @Value("${spring.security.jwt.refresh-expiration}")
    private long refreshTokenExpiration;

    /**
     * Generate access token for user.
     *
     * @param user the user
     * @return JWT access token
     */
    public String generateAccessToken(final User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("role", user.getRole().name());
        return generateToken(claims, user.getEmail(), accessTokenExpiration);
    }

    /**
     * Generate refresh token for user.
     *
     * @param user the user
     * @return JWT refresh token
     */
    public String generateRefreshToken(final User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return generateToken(claims, user.getEmail(), refreshTokenExpiration);
    }

    /**
     * Extract email from token.
     *
     * @param token JWT token
     * @return email
     */
    public String extractEmail(final String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract email from refresh token.
     *
     * @param refreshToken JWT refresh token
     * @return email
     */
    public String extractEmailFromRefreshToken(final String refreshToken) {
        return extractEmail(refreshToken);
    }

    /**
     * Extract expiration date from token.
     *
     * @param token JWT token
     * @return expiration date
     */
    public Date extractExpiration(final String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract specific claim from token.
     *
     * @param token JWT token
     * @param claimsResolver function to extract claim
     * @param <T> claim type
     * @return extracted claim
     */
    public <T> T extractClaim(final String token, final Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Validate token against user details.
     *
     * @param token JWT token
     * @param userDetails user details
     * @return true if token is valid
     */
    public boolean isTokenValid(final String token, final UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Validate refresh token.
     *
     * @param refreshToken JWT refresh token
     * @return true if refresh token is valid
     */
    public boolean isValidRefreshToken(final String refreshToken) {
        try {
            Claims claims = extractAllClaims(refreshToken);
            String type = claims.get("type", String.class);
            return "refresh".equals(type) && !isTokenExpired(refreshToken);
        } catch (Exception e) {
            log.error("Invalid refresh token", e);
            return false;
        }
    }

    /**
     * Get access token expiration time.
     *
     * @return expiration time in seconds
     */
    public long getAccessTokenExpiration() {
        return accessTokenExpiration / 1000; // Convert to seconds
    }

    /**
     * Check if token is expired.
     *
     * @param token JWT token
     * @return true if token is expired
     */
    private boolean isTokenExpired(final String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Generate JWT token.
     *
     * @param extraClaims additional claims
     * @param subject token subject (usually email)
     * @param expiration expiration time in milliseconds
     * @return JWT token
     */
    private String generateToken(final Map<String, Object> extraClaims, 
                                final String subject, 
                                final long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extract user ID from token.
     *
     * @param token JWT token
     * @return user ID
     */
    public Long extractUserId(final String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Long.class);
    }

    /**
     * Extract user role from token.
     *
     * @param token JWT token
     * @return user role
     */
    public String extractUserRole(final String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }

    /**
     * Extract all claims from token.
     *
     * @param token JWT token
     * @return claims
     */
    private Claims extractAllClaims(final String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Get signing key for JWT.
     *
     * @return secret key
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
