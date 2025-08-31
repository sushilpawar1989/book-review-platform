package com.bookreview.dto.user;

import com.bookreview.book.Genre;
import com.bookreview.dto.review.ReviewDTO;
import com.bookreview.user.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * DTO for user profile information including reviews.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String bio;
    private UserRole role;
    private Set<Genre> preferredGenres;
    private LocalDateTime createdAt;
    
    // Profile statistics
    private Integer totalReviews;
    private Integer totalFavoriteBooks;
    private Double averageRating; // Average rating given by this user
    
    // Recent reviews written by the user
    private List<ReviewDTO> recentReviews;
}
