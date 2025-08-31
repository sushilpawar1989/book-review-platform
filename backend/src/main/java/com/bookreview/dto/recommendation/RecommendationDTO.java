package com.bookreview.dto.recommendation;

import com.bookreview.dto.book.BookDTO;
import com.bookreview.recommendation.RecommendationStrategy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for recommendation information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDTO {

    private Long id;
    private Long userId;
    private BookDTO book;
    private RecommendationStrategy strategy; // TOP_RATED, GENRE_BASED, AI_POWERED
    private String reason; // Human-readable explanation
    private Double score; // Recommendation confidence score (0.0 - 1.0)
    private LocalDateTime createdAt;
}
