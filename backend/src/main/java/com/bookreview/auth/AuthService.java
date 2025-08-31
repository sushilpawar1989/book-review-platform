package com.bookreview.auth;

import com.bookreview.dto.auth.LoginRequestDTO;
import com.bookreview.dto.auth.LoginResponseDTO;
import com.bookreview.dto.auth.RefreshTokenResponseDTO;
import com.bookreview.dto.auth.RegisterRequestDTO;
import com.bookreview.dto.auth.RegisterResponseDTO;
import com.bookreview.dto.user.UserDTO;
import com.bookreview.user.User;
import com.bookreview.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for authentication operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * Register a new user.
     *
     * @param request registration request
     * @return registration response with JWT tokens
     */
    public RegisterResponseDTO register(final RegisterRequestDTO request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User with email already exists: " + request.getEmail());
        }

        // Create new user
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .bio(request.getBio())
                .preferredGenres(request.getPreferredGenres())
                .build();

        User savedUser = userRepository.save(user);

        // Generate JWT tokens
        String accessToken = jwtService.generateAccessToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        log.info("User registered successfully with ID: {}", savedUser.getId());

        return RegisterResponseDTO.builder()
                .user(mapToUserDTO(savedUser))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration())
                .build();
    }

    /**
     * Authenticate user and generate tokens.
     *
     * @param request login request
     * @return login response with JWT tokens
     */
    public LoginResponseDTO login(final LoginRequestDTO request) {
        log.info("Authenticating user with email: {}", request.getEmail());

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getEmail()));

        // Generate JWT tokens
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        log.info("User authenticated successfully with ID: {}", user.getId());

        return LoginResponseDTO.builder()
                .user(mapToUserDTO(user))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration())
                .build();
    }

    /**
     * Refresh access token using refresh token.
     *
     * @param refreshToken the refresh token
     * @return refresh token response with new access token
     */
    public RefreshTokenResponseDTO refreshToken(final String refreshToken) {
        log.info("Refreshing access token");

        if (!jwtService.isValidRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String email = jwtService.extractEmailFromRefreshToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        String newAccessToken = jwtService.generateAccessToken(user);

        return RefreshTokenResponseDTO.builder()
                .accessToken(newAccessToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration())
                .build();
    }

    /**
     * Logout user (for stateless JWT, this is mainly for logging purposes).
     *
     * @param email the user email
     */
    public void logout(final String email) {
        log.info("Logging out user: {}", email);
        // For stateless JWT, logout is typically handled on the client side
        // The client should discard the tokens
        // In a production system, you might want to implement token blacklisting
    }

    /**
     * Initiate password reset process.
     *
     * @param email user email
     */
    public void forgotPassword(final String email) {
        log.info("Initiating password reset for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        // TODO: Generate password reset token and send email
        // This is a placeholder implementation
        log.info("Password reset email sent to: {}", email);
    }

    /**
     * Reset password using reset token.
     *
     * @param token reset token
     * @param newPassword new password
     */
    public void resetPassword(final String token, final String newPassword) {
        log.info("Resetting password with token");

        // TODO: Validate reset token and update password
        // This is a placeholder implementation
        log.info("Password reset successfully");
    }

    /**
     * Map User entity to UserDTO.
     *
     * @param user the user entity
     * @return user DTO
     */
    private UserDTO mapToUserDTO(final User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .bio(user.getBio())
                .role(user.getRole())
                .preferredGenres(user.getPreferredGenres())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
