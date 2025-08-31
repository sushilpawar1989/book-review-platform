package com.bookreview.recommendation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Recommendation entity operations.
 */
@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    /**
     * Find recommendations for a user.
     *
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of recommendations
     */
    Page<Recommendation> findByUserId(Long userId, Pageable pageable);

    /**
     * Find valid (non-expired) recommendations for a user.
     *
     * @param userId the user ID
     * @param now current timestamp
     * @param pageable pagination information
     * @return page of valid recommendations
     */
    @Query("SELECT r FROM Recommendation r WHERE r.user.id = :userId AND (r.expiresAt IS NULL OR r.expiresAt > :now)")
    Page<Recommendation> findValidRecommendationsForUser(@Param("userId") Long userId, 
                                                         @Param("now") LocalDateTime now, 
                                                         Pageable pageable);

    /**
     * Find recommendations by strategy for a user.
     *
     * @param userId the user ID
     * @param strategy the recommendation strategy
     * @param pageable pagination information
     * @return page of recommendations
     */
    Page<Recommendation> findByUserIdAndStrategy(Long userId, RecommendationStrategy strategy, Pageable pageable);

    /**
     * Find valid recommendations by strategy for a user.
     *
     * @param userId the user ID
     * @param strategy the recommendation strategy
     * @param now current timestamp
     * @param pageable pagination information
     * @return page of valid recommendations
     */
    @Query("SELECT r FROM Recommendation r WHERE r.user.id = :userId AND r.strategy = :strategy " +
           "AND (r.expiresAt IS NULL OR r.expiresAt > :now)")
    Page<Recommendation> findValidRecommendationsByStrategy(@Param("userId") Long userId, 
                                                            @Param("strategy") RecommendationStrategy strategy,
                                                            @Param("now") LocalDateTime now, 
                                                            Pageable pageable);

    /**
     * Find recommendations for a book.
     *
     * @param bookId the book ID
     * @param pageable pagination information
     * @return page of recommendations
     */
    Page<Recommendation> findByBookId(Long bookId, Pageable pageable);

    /**
     * Find recommendation by user and book.
     *
     * @param userId the user ID
     * @param bookId the book ID
     * @return optional recommendation
     */
    Optional<Recommendation> findByUserIdAndBookId(Long userId, Long bookId);

    /**
     * Check if recommendation exists for user and book.
     *
     * @param userId the user ID
     * @param bookId the book ID
     * @return true if recommendation exists
     */
    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    /**
     * Find recommendations with high confidence.
     *
     * @param userId the user ID
     * @param minConfidence minimum confidence threshold
     * @param pageable pagination information
     * @return page of high-confidence recommendations
     */
    @Query("SELECT r FROM Recommendation r WHERE r.user.id = :userId AND r.confidence >= :minConfidence")
    Page<Recommendation> findHighConfidenceRecommendations(@Param("userId") Long userId, 
                                                           @Param("minConfidence") Double minConfidence, 
                                                           Pageable pageable);

    /**
     * Find expired recommendations.
     *
     * @param now current timestamp
     * @param pageable pagination information
     * @return page of expired recommendations
     */
    @Query("SELECT r FROM Recommendation r WHERE r.expiresAt IS NOT NULL AND r.expiresAt <= :now")
    Page<Recommendation> findExpiredRecommendations(@Param("now") LocalDateTime now, Pageable pageable);

    /**
     * Find recommendations created after specified date.
     *
     * @param date the date
     * @param pageable pagination information
     * @return page of recent recommendations
     */
    Page<Recommendation> findByCreatedAtAfter(LocalDateTime date, Pageable pageable);

    /**
     * Count recommendations by user.
     *
     * @param userId the user ID
     * @return count of recommendations
     */
    long countByUserId(Long userId);

    /**
     * Count recommendations by strategy.
     *
     * @param strategy the recommendation strategy
     * @return count of recommendations
     */
    long countByStrategy(RecommendationStrategy strategy);

    /**
     * Count valid recommendations by user.
     *
     * @param userId the user ID
     * @param now current timestamp
     * @return count of valid recommendations
     */
    @Query("SELECT COUNT(r) FROM Recommendation r WHERE r.user.id = :userId " +
           "AND (r.expiresAt IS NULL OR r.expiresAt > :now)")
    long countValidRecommendationsByUser(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    /**
     * Get average confidence by strategy.
     *
     * @param strategy the recommendation strategy
     * @return average confidence
     */
    @Query("SELECT AVG(r.confidence) FROM Recommendation r WHERE r.strategy = :strategy")
    Optional<Double> getAverageConfidenceByStrategy(@Param("strategy") RecommendationStrategy strategy);

    /**
     * Find top recommendations for user (by confidence).
     *
     * @param userId the user ID
     * @param now current timestamp
     * @param pageable pagination information
     * @return page of top recommendations
     */
    @Query("SELECT r FROM Recommendation r WHERE r.user.id = :userId " +
           "AND (r.expiresAt IS NULL OR r.expiresAt > :now) " +
           "ORDER BY r.confidence DESC")
    Page<Recommendation> findTopRecommendationsForUser(@Param("userId") Long userId, 
                                                       @Param("now") LocalDateTime now, 
                                                       Pageable pageable);

    /**
     * Find recommendations by multiple strategies.
     *
     * @param userId the user ID
     * @param strategies list of strategies
     * @param pageable pagination information
     * @return page of recommendations
     */
    @Query("SELECT r FROM Recommendation r WHERE r.user.id = :userId AND r.strategy IN :strategies")
    Page<Recommendation> findByUserIdAndStrategyIn(@Param("userId") Long userId, 
                                                   @Param("strategies") List<RecommendationStrategy> strategies, 
                                                   Pageable pageable);

    /**
     * Delete expired recommendations.
     *
     * @param now current timestamp
     * @return number of deleted recommendations
     */
    @Query("DELETE FROM Recommendation r WHERE r.expiresAt IS NOT NULL AND r.expiresAt <= :now")
    int deleteExpiredRecommendations(@Param("now") LocalDateTime now);

    /**
     * Delete recommendations by user ID.
     *
     * @param userId the user ID
     */
    void deleteByUserId(Long userId);

    /**
     * Delete recommendations by book ID.
     *
     * @param bookId the book ID
     */
    void deleteByBookId(Long bookId);

    /**
     * Delete recommendations older than specified date.
     *
     * @param date the cutoff date
     * @return number of deleted recommendations
     */
    @Query("DELETE FROM Recommendation r WHERE r.createdAt < :date")
    int deleteOldRecommendations(@Param("date") LocalDateTime date);
}
