package com.bookreview.dto.auth;

import com.bookreview.dto.user.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for registration response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponseDTO {

    private UserDTO user;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private long expiresIn;
}
