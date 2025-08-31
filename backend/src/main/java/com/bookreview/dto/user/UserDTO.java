package com.bookreview.dto.user;

import com.bookreview.book.Genre;
import com.bookreview.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for user information (without sensitive data).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String bio;
    private UserRole role;
    private Set<Genre> preferredGenres;
    private LocalDateTime createdAt;

    /**
     * Get user's full name.
     *
     * @return concatenated first and last name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
