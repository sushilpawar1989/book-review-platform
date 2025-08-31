package com.bookreview.integration;

import com.bookreview.dto.auth.LoginRequestDTO;
import com.bookreview.dto.auth.RegisterRequestDTO;
import com.bookreview.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for AuthController.
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void register_ShouldCreateUserAndReturnTokens() throws Exception {
        // Given
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        RegisterRequestDTO registerRequest = RegisterRequestDTO.builder()
                .email("test@example.com")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .bio("Test user")
                .preferredGenres(Set.of())
                .build();

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void register_ShouldReturnBadRequest_WhenEmailIsInvalid() throws Exception {
        // Given
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        RegisterRequestDTO registerRequest = RegisterRequestDTO.builder()
                .email("invalid-email")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .build();

        // When & Then
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ShouldReturnTokens_WhenCredentialsAreValid() throws Exception {
        // Given
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // First register a user
        RegisterRequestDTO registerRequest = RegisterRequestDTO.builder()
                .email("test@example.com")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .build();

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        LoginRequestDTO loginRequest = LoginRequestDTO.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void login_ShouldReturnUnauthorized_WhenCredentialsAreInvalid() throws Exception {
        // Given
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        LoginRequestDTO loginRequest = LoginRequestDTO.builder()
                .email("nonexistent@example.com")
                .password("wrongpassword")
                .build();

        // When & Then
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}
