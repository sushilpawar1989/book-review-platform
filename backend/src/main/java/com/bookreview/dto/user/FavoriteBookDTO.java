package com.bookreview.dto.user;

import com.bookreview.book.Genre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for favorite book information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteBookDTO {

    private Long id;
    private String title;
    private String author;
    private String description;
    private String coverImageUrl;
    private Set<Genre> genres;
    private Integer publishedYear;
    private BigDecimal averageRating;
    private Integer totalReviews;
    private LocalDateTime addedToFavoritesAt; // When user added to favorites
    private Boolean hasUserReviewed; // Whether current user has reviewed this book
}
