package com.bookreview.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for reset password request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequestDTO {

    @NotBlank(message = "Token is mandatory")
    private String token;

    @NotBlank(message = "New password is mandatory")
    @Size(min = 8, message = "Password should have at least 8 characters")
    private String newPassword;
}
