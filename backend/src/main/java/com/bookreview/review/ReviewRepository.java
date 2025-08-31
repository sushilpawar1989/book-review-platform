package com.bookreview.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Review entity operations.
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Find reviews by book ID.
     *
     * @param bookId book ID
     * @param pageable pagination information
     * @return page of reviews
     */
    Page<Review> findByBookId(Long bookId, Pageable pageable);

    /**
     * Find reviews by user ID.
     *
     * @param userId user ID
     * @param pageable pagination information
     * @return page of reviews
     */
    Page<Review> findByUserId(Long userId, Pageable pageable);

    /**
     * Find review by book ID and user ID.
     *
     * @param bookId book ID
     * @param userId user ID
     * @return optional review
     */
    Optional<Review> findByBookIdAndUserId(Long bookId, Long userId);

    /**
     * Check if a review exists for a book by a user.
     *
     * @param bookId book ID
     * @param userId user ID
     * @return true if review exists
     */
    boolean existsByBookIdAndUserId(Long bookId, Long userId);

    /**
     * Find reviews by rating.
     *
     * @param rating the rating
     * @param pageable pagination information
     * @return page of reviews
     */
    Page<Review> findByRating(Integer rating, Pageable pageable);

    /**
     * Find reviews with rating greater than or equal to minimum.
     *
     * @param minRating minimum rating
     * @param pageable pagination information
     * @return page of reviews
     */
    Page<Review> findByRatingGreaterThanEqual(Integer minRating, Pageable pageable);

    /**
     * Find reviews with rating between min and max.
     *
     * @param minRating minimum rating
     * @param maxRating maximum rating
     * @param pageable pagination information
     * @return page of reviews
     */
    Page<Review> findByRatingBetween(Integer minRating, Integer maxRating, Pageable pageable);

    /**
     * Count reviews by book ID.
     *
     * @param bookId book ID
     * @return number of reviews
     */
    long countByBookId(Long bookId);

    /**
     * Count reviews by user ID.
     *
     * @param userId user ID
     * @return number of reviews
     */
    long countByUserId(Long userId);

    /**
     * Get average rating for a book.
     *
     * @param bookId book ID
     * @return average rating or null if no reviews
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.id = :bookId")
    Double getAverageRatingByBookId(@Param("bookId") Long bookId);

    /**
     * Delete all reviews by book ID.
     *
     * @param bookId book ID
     */
    void deleteByBookId(Long bookId);

    /**
     * Delete all reviews by user ID.
     *
     * @param userId user ID
     */
    void deleteByUserId(Long userId);

    /**
     * Get top-rated reviews.
     *
     * @param minRating minimum rating
     * @param pageable pagination information
     * @return page of highly rated reviews
     */
    @Query("SELECT r FROM Review r WHERE r.rating >= :minRating ORDER BY r.rating DESC, r.createdAt DESC")
    Page<Review> findTopRatedReviews(@Param("minRating") Integer minRating, Pageable pageable);

    /**
     * Find recent reviews.
     *
     * @param sinceDate since date
     * @param pageable pagination information
     * @return page of recent reviews
     */
    @Query("SELECT r FROM Review r WHERE r.createdAt >= :sinceDate ORDER BY r.createdAt DESC")
    Page<Review> findRecentReviews(@Param("sinceDate") java.time.LocalDateTime sinceDate, Pageable pageable);

    /**
     * Get average rating given by a user across all their reviews.
     *
     * @param userId user ID
     * @return average rating given by user or null if no reviews
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.user.id = :userId")
    Double getAverageRatingByUserId(@Param("userId") Long userId);
}