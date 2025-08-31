package com.bookreview.dto.user;

import com.bookreview.book.Genre;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO for updating user profile information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateProfileDTO {

    @Size(max = 50, message = "First name should not exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name should not exceed 50 characters")
    private String lastName;

    @Size(max = 500, message = "Bio should not exceed 500 characters")
    private String bio;

    private Set<Genre> preferredGenres;
}
