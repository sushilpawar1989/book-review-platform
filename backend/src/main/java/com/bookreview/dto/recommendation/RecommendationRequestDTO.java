package com.bookreview.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for recommendation request parameters.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationRequestDTO {

    @Builder.Default
    private Integer limit = 10; // Number of recommendations to return
    @Builder.Default
    private Boolean includeTopRated = true;
    @Builder.Default
    private Boolean includeGenreBased = true;
    @Builder.Default
    private Boolean includeAIPowered = false; // Enable when OpenAI integration is ready
    @Builder.Default
    private Double minRating = 3.5; // Minimum book rating to consider
    @Builder.Default
    private Integer minReviews = 5; // Minimum number of reviews to consider
}
