package com.bookreview.recommendation;

import com.bookreview.dto.recommendation.RecommendationDTO;
import com.bookreview.dto.recommendation.RecommendationRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for recommendation endpoints.
 */
@RestController
@RequestMapping("/api/v1/recommendations")
@RequiredArgsConstructor
@Slf4j
public class RecommendationController {

    private final RecommendationService recommendationService;

    /**
     * Get personalized recommendations for the current user.
     *
     * @param limit number of recommendations (default: 10)
     * @param minRating minimum book rating (default: 3.5)
     * @param minReviews minimum number of reviews (default: 5)
     * @param includeTopRated include top-rated books (default: true)
     * @param includeGenreBased include genre-based recommendations (default: true)
     * @param includeAIPowered include AI-powered recommendations (default: false)
     * @return list of recommendations
     */
    @GetMapping("/for-me")
    public ResponseEntity<List<RecommendationDTO>> getRecommendationsForCurrentUser(
            @RequestParam(defaultValue = "10") final Integer limit,
            @RequestParam(defaultValue = "3.5") final Double minRating,
            @RequestParam(defaultValue = "5") final Integer minReviews,
            @RequestParam(defaultValue = "true") final Boolean includeTopRated,
            @RequestParam(defaultValue = "true") final Boolean includeGenreBased,
            @RequestParam(defaultValue = "false") final Boolean includeAIPowered) {

        log.debug("Getting recommendations for current user - limit: {}, minRating: {}, minReviews: {}", 
                  limit, minRating, minReviews);

        try {
            RecommendationRequestDTO requestDTO = RecommendationRequestDTO.builder()
                    .limit(limit)
                    .minRating(minRating)
                    .minReviews(minReviews)
                    .includeTopRated(includeTopRated)
                    .includeGenreBased(includeGenreBased)
                    .includeAIPowered(includeAIPowered)
                    .build();

            List<RecommendationDTO> recommendations = recommendationService.getRecommendationsForCurrentUser(requestDTO);
            return ResponseEntity.ok(recommendations);

        } catch (AccessDeniedException e) {
            log.error("Access denied getting recommendations: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            log.error("Error getting recommendations: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get personalized recommendations for the current user (POST version with full request body).
     *
     * @param requestDTO recommendation request parameters
     * @return list of recommendations
     */
    @PostMapping("/for-me")
    public ResponseEntity<List<RecommendationDTO>> getRecommendationsForCurrentUserPost(
            @RequestBody final RecommendationRequestDTO requestDTO) {

        log.debug("Getting recommendations for current user with request: {}", requestDTO);

        try {
            List<RecommendationDTO> recommendations = recommendationService.getRecommendationsForCurrentUser(requestDTO);
            return ResponseEntity.ok(recommendations);

        } catch (AccessDeniedException e) {
            log.error("Access denied getting recommendations: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            log.error("Error getting recommendations: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get personalized recommendations for a specific user.
     *
     * @param userId user ID
     * @param limit number of recommendations (default: 10)
     * @param minRating minimum book rating (default: 3.5)
     * @param minReviews minimum number of reviews (default: 5)
     * @param includeTopRated include top-rated books (default: true)
     * @param includeGenreBased include genre-based recommendations (default: true)
     * @param includeAIPowered include AI-powered recommendations (default: false)
     * @return list of recommendations
     */
    @GetMapping("/for-user/{userId}")
    public ResponseEntity<List<RecommendationDTO>> getRecommendationsForUser(
            @PathVariable final Long userId,
            @RequestParam(defaultValue = "10") final Integer limit,
            @RequestParam(defaultValue = "3.5") final Double minRating,
            @RequestParam(defaultValue = "5") final Integer minReviews,
            @RequestParam(defaultValue = "true") final Boolean includeTopRated,
            @RequestParam(defaultValue = "true") final Boolean includeGenreBased,
            @RequestParam(defaultValue = "false") final Boolean includeAIPowered) {

        log.debug("Getting recommendations for user: {} - limit: {}, minRating: {}, minReviews: {}", 
                  userId, limit, minRating, minReviews);

        try {
            RecommendationRequestDTO requestDTO = RecommendationRequestDTO.builder()
                    .limit(limit)
                    .minRating(minRating)
                    .minReviews(minReviews)
                    .includeTopRated(includeTopRated)
                    .includeGenreBased(includeGenreBased)
                    .includeAIPowered(includeAIPowered)
                    .build();

            List<RecommendationDTO> recommendations = recommendationService.getRecommendationsForUser(userId, requestDTO);
            return ResponseEntity.ok(recommendations);

        } catch (IllegalArgumentException e) {
            log.error("Error getting recommendations for user: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error getting recommendations: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get simple top-rated book recommendations (public endpoint).
     *
     * @param limit number of recommendations (default: 10)
     * @param minRating minimum book rating (default: 4.0)
     * @param minReviews minimum number of reviews (default: 10)
     * @return list of top-rated recommendations
     */
    @GetMapping("/top-rated")
    public ResponseEntity<List<RecommendationDTO>> getTopRatedRecommendations(
            @RequestParam(defaultValue = "10") final Integer limit,
            @RequestParam(defaultValue = "4.0") final Double minRating,
            @RequestParam(defaultValue = "10") final Integer minReviews) {

        log.debug("Getting top-rated recommendations - limit: {}, minRating: {}, minReviews: {}", 
                  limit, minRating, minReviews);

        try {
            // Create a generic request for top-rated books only
            RecommendationRequestDTO requestDTO = RecommendationRequestDTO.builder()
                    .limit(limit)
                    .minRating(minRating)
                    .minReviews(minReviews)
                    .includeTopRated(true)
                    .includeGenreBased(false)
                    .includeAIPowered(false)
                    .build();

            // Use a dummy user ID (1L) for public recommendations
            // In a real scenario, you might want to create a separate method for anonymous users
            List<RecommendationDTO> recommendations = recommendationService.getRecommendationsForUser(1L, requestDTO);
            
            // Remove user-specific information for public endpoint
            recommendations.forEach(rec -> rec.setUserId(null));
            
            return ResponseEntity.ok(recommendations);

        } catch (Exception e) {
            log.error("Error getting top-rated recommendations: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}