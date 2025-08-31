package com.bookreview.auth;

import com.bookreview.dto.auth.ForgotPasswordRequestDTO;
import com.bookreview.dto.auth.LoginRequestDTO;
import com.bookreview.dto.auth.LoginResponseDTO;
import com.bookreview.dto.auth.RefreshTokenRequestDTO;
import com.bookreview.dto.auth.RefreshTokenResponseDTO;
import com.bookreview.dto.auth.RegisterRequestDTO;
import com.bookreview.dto.auth.RegisterResponseDTO;
import com.bookreview.dto.auth.ResetPasswordRequestDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for authentication endpoints.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user.
     *
     * @param request registration request
     * @return registration response with JWT tokens
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody final RegisterRequestDTO request) {
        log.info("User registration request for email: {}", request.getEmail());
        
        RegisterResponseDTO response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticate user and return JWT tokens.
     *
     * @param request login request
     * @return login response with JWT tokens
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody final LoginRequestDTO request) {
        log.info("User login request for email: {}", request.getEmail());
        
        LoginResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Refresh access token using refresh token.
     *
     * @param request refresh token request
     * @return new access token
     */
    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponseDTO> refreshToken(@Valid @RequestBody final RefreshTokenRequestDTO request) {
        log.info("Token refresh request");
        
        RefreshTokenResponseDTO response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }

    /**
     * Logout user and invalidate tokens.
     *
     * @param authentication user authentication
     * @return success response
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(final Authentication authentication) {
        log.info("User logout request for: {}", authentication.getName());
        
        authService.logout(authentication.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * Send password reset email.
     *
     * @param request forgot password request
     * @return success response
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@Valid @RequestBody final ForgotPasswordRequestDTO request) {
        log.info("Password reset request for email: {}", request.getEmail());
        
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok().build();
    }

    /**
     * Reset password using reset token.
     *
     * @param request reset password request
     * @return success response
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody final ResetPasswordRequestDTO request) {
        log.info("Password reset confirmation");
        
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok().build();
    }
}
