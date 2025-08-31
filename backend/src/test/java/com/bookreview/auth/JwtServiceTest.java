package com.bookreview.auth;

import com.bookreview.user.User;
import com.bookreview.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for JwtService.
 */
class JwtServiceTest {

    private JwtService jwtService;
    private User testUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        
        // Set private fields using reflection for testing
        ReflectionTestUtils.setField(jwtService, "secret", "testSecretKeyForJwtThatIsLongEnoughForHS256Algorithm");
        ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", 3600000L); // 1 hour
        ReflectionTestUtils.setField(jwtService, "refreshTokenExpiration", 7200000L); // 2 hours

        testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .role(UserRole.USER)
                .build();
    }

    @Test
    void generateAccessToken_ShouldReturnValidToken() {
        // When
        String token = jwtService.generateAccessToken(testUser);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        String extractedEmail = jwtService.extractEmail(token);
        assertEquals(testUser.getEmail(), extractedEmail);
        
        Long extractedUserId = jwtService.extractUserId(token);
        assertEquals(testUser.getId(), extractedUserId);
        
        String extractedRole = jwtService.extractUserRole(token);
        assertEquals(testUser.getRole().name(), extractedRole);
    }

    @Test
    void generateRefreshToken_ShouldReturnValidToken() {
        // When
        String refreshToken = jwtService.generateRefreshToken(testUser);

        // Then
        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());
        assertTrue(jwtService.isValidRefreshToken(refreshToken));
        
        String extractedEmail = jwtService.extractEmailFromRefreshToken(refreshToken);
        assertEquals(testUser.getEmail(), extractedEmail);
    }

    @Test
    void isTokenValid_ShouldReturnTrue_WhenTokenIsValidAndUserMatches() {
        // Given
        String token = jwtService.generateAccessToken(testUser);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(testUser.getEmail());

        // When
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Then
        assertTrue(isValid);
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenUserDoesNotMatch() {
        // Given
        String token = jwtService.generateAccessToken(testUser);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("different@example.com");

        // When
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Then
        assertFalse(isValid);
    }

    @Test
    void isValidRefreshToken_ShouldReturnTrue_WhenRefreshTokenIsValid() {
        // Given
        String refreshToken = jwtService.generateRefreshToken(testUser);

        // When
        boolean isValid = jwtService.isValidRefreshToken(refreshToken);

        // Then
        assertTrue(isValid);
    }

    @Test
    void isValidRefreshToken_ShouldReturnFalse_WhenTokenIsNotRefreshToken() {
        // Given
        String accessToken = jwtService.generateAccessToken(testUser);

        // When
        boolean isValid = jwtService.isValidRefreshToken(accessToken);

        // Then
        assertFalse(isValid);
    }

    @Test
    void getAccessTokenExpiration_ShouldReturnCorrectValue() {
        // When
        long expiration = jwtService.getAccessTokenExpiration();

        // Then
        assertEquals(3600L, expiration); // 1 hour in seconds
    }
}
