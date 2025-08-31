package com.bookreview.auth;

import com.bookreview.dto.auth.LoginRequestDTO;
import com.bookreview.dto.auth.LoginResponseDTO;
import com.bookreview.dto.auth.RefreshTokenResponseDTO;
import com.bookreview.dto.auth.RegisterRequestDTO;
import com.bookreview.dto.auth.RegisterResponseDTO;
import com.bookreview.user.User;
import com.bookreview.user.UserRepository;
import com.bookreview.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AuthService.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private RegisterRequestDTO registerRequest;
    private LoginRequestDTO loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequestDTO.builder()
                .email("test@example.com")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .bio("Test user")
                .preferredGenres(Set.of())
                .build();

        loginRequest = LoginRequestDTO.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("John")
                .lastName("Doe")
                .role(UserRole.USER)
                .build();
    }

    @Test
    void register_ShouldCreateUserAndReturnTokens() {
        // Given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token");
        when(jwtService.getAccessTokenExpiration()).thenReturn(3600L);

        // When
        RegisterResponseDTO response = authService.register(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600L, response.getExpiresIn());
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        // Given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest));
    }

    @Test
    void login_ShouldReturnTokens_WhenCredentialsAreValid() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token");
        when(jwtService.getAccessTokenExpiration()).thenReturn(3600L);

        // When
        LoginResponseDTO response = authService.login(loginRequest);

        // Then
        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
    }

    @Test
    void refreshToken_ShouldReturnNewAccessToken_WhenRefreshTokenIsValid() {
        // Given
        String refreshToken = "valid-refresh-token";
        when(jwtService.isValidRefreshToken(refreshToken)).thenReturn(true);
        when(jwtService.extractEmailFromRefreshToken(refreshToken)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("new-access-token");
        when(jwtService.getAccessTokenExpiration()).thenReturn(3600L);

        // When
        RefreshTokenResponseDTO response = authService.refreshToken(refreshToken);

        // Then
        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600L, response.getExpiresIn());
    }

    @Test
    void refreshToken_ShouldThrowException_WhenRefreshTokenIsInvalid() {
        // Given
        String refreshToken = "invalid-refresh-token";
        when(jwtService.isValidRefreshToken(refreshToken)).thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> authService.refreshToken(refreshToken));
    }
}
