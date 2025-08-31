package com.bookreview.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO class for user statistics.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStats {
    private Long userId;
    private long totalReviews;
    private long totalFavorites;
    private double averageRatingGiven;
}
