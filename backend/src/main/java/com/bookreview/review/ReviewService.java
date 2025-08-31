package com.bookreview.review;

import com.bookreview.book.Book;
import com.bookreview.book.BookRepository;
import com.bookreview.dto.review.ReviewCreateDTO;
import com.bookreview.dto.review.ReviewDTO;
import com.bookreview.dto.review.ReviewUpdateDTO;
import com.bookreview.user.User;
import com.bookreview.user.UserRepository;
import com.bookreview.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service class for review operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    /**
     * Get all reviews with pagination.
     *
     * @param pageable pagination information
     * @return page of review DTOs
     */
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getAllReviews(final Pageable pageable) {
        log.debug("Getting all reviews with pagination: {}", pageable);
        return reviewRepository.findAll(pageable).map(this::mapToDTO);
    }

    /**
     * Get review by ID.
     *
     * @param id review ID
     * @return optional review DTO
     */
    @Transactional(readOnly = true)
    public Optional<ReviewDTO> getReviewById(final Long id) {
        log.debug("Getting review by ID: {}", id);
        return reviewRepository.findById(id).map(this::mapToDTO);
    }

    /**
     * Create a new review.
     *
     * @param createDTO review creation data
     * @return created review DTO
     */
    public ReviewDTO createReview(final ReviewCreateDTO createDTO) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("User must be authenticated to create a review");
        }

        log.info("Creating new review for book ID: {} by user ID: {}", createDTO.getBookId(), currentUserId);

        // Check if book exists
        Book book = bookRepository.findById(createDTO.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + createDTO.getBookId()));

        // Check if user exists
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + currentUserId));

        // Check if user already reviewed this book
        if (reviewRepository.existsByBookIdAndUserId(createDTO.getBookId(), currentUserId)) {
            throw new IllegalArgumentException("User has already reviewed this book");
        }

        // Create review
        Review review = Review.builder()
                .book(book)
                .user(user)
                .rating(createDTO.getRating())
                .text(createDTO.getText())
                .build();

        Review savedReview = reviewRepository.save(review);
        log.info("Review created successfully with ID: {}", savedReview.getId());

        // Update book's average rating
        updateBookRating(createDTO.getBookId());

        return mapToDTO(savedReview);
    }

    /**
     * Update an existing review.
     *
     * @param id review ID
     * @param updateDTO review update data
     * @return updated review DTO
     */
    public ReviewDTO updateReview(final Long id, final ReviewUpdateDTO updateDTO) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("User must be authenticated to update a review");
        }

        log.info("Updating review with ID: {} by user ID: {}", id, currentUserId);

        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + id));

        // Check if current user owns this review
        if (!existingReview.belongsToUser(currentUserId)) {
            throw new AccessDeniedException("User can only update their own reviews");
        }

        // Update fields if provided
        boolean ratingChanged = false;
        if (updateDTO.getRating() != null && !updateDTO.getRating().equals(existingReview.getRating())) {
            existingReview.setRating(updateDTO.getRating());
            ratingChanged = true;
        }
        if (updateDTO.getText() != null) {
            existingReview.setText(updateDTO.getText());
        }

        Review savedReview = reviewRepository.save(existingReview);
        log.info("Review updated successfully");

        // Update book's average rating if rating changed
        if (ratingChanged) {
            updateBookRating(existingReview.getBook().getId());
        }

        return mapToDTO(savedReview);
    }

    /**
     * Delete a review.
     *
     * @param id review ID
     */
    public void deleteReview(final Long id) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("User must be authenticated to delete a review");
        }

        log.info("Deleting review with ID: {} by user ID: {}", id, currentUserId);

        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Review not found: " + id));

        // Check if current user owns this review or is admin
        if (!review.belongsToUser(currentUserId) && !SecurityUtils.isAdmin()) {
            throw new AccessDeniedException("User can only delete their own reviews");
        }

        Long bookId = review.getBook().getId();
        reviewRepository.deleteById(id);
        log.info("Review deleted successfully");

        // Update book's average rating
        updateBookRating(bookId);
    }

    /**
     * Get reviews by book ID.
     *
     * @param bookId book ID
     * @param pageable pagination information
     * @return page of review DTOs
     */
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviewsByBookId(final Long bookId, final Pageable pageable) {
        log.debug("Getting reviews for book ID: {}", bookId);
        
        // Verify book exists
        if (!bookRepository.existsById(bookId)) {
            throw new IllegalArgumentException("Book not found: " + bookId);
        }
        
        return reviewRepository.findByBookId(bookId, pageable).map(this::mapToDTO);
    }

    /**
     * Get reviews by user ID.
     *
     * @param userId user ID
     * @param pageable pagination information
     * @return page of review DTOs
     */
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getReviewsByUserId(final Long userId, final Pageable pageable) {
        log.debug("Getting reviews for user ID: {}", userId);
        
        // Verify user exists
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        
        return reviewRepository.findByUserId(userId, pageable).map(this::mapToDTO);
    }

    /**
     * Get current user's reviews.
     *
     * @param pageable pagination information
     * @return page of review DTOs
     */
    @Transactional(readOnly = true)
    public Page<ReviewDTO> getCurrentUserReviews(final Pageable pageable) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("User must be authenticated to view their reviews");
        }
        
        log.debug("Getting reviews for current user ID: {}", currentUserId);
        return reviewRepository.findByUserId(currentUserId, pageable).map(this::mapToDTO);
    }

    /**
     * Get user's review for a specific book.
     *
     * @param bookId book ID
     * @param userId user ID
     * @return optional review DTO
     */
    @Transactional(readOnly = true)
    public Optional<ReviewDTO> getUserReviewForBook(final Long bookId, final Long userId) {
        log.debug("Getting review for book ID: {} by user ID: {}", bookId, userId);
        return reviewRepository.findByBookIdAndUserId(bookId, userId).map(this::mapToDTO);
    }

    /**
     * Get current user's review for a specific book.
     *
     * @param bookId book ID
     * @return optional review DTO
     */
    @Transactional(readOnly = true)
    public Optional<ReviewDTO> getCurrentUserReviewForBook(final Long bookId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return Optional.empty();
        }
        
        return getUserReviewForBook(bookId, currentUserId);
    }

    /**
     * Check if current user has reviewed a book.
     *
     * @param bookId book ID
     * @return true if user has reviewed the book
     */
    @Transactional(readOnly = true)
    public boolean hasCurrentUserReviewedBook(final Long bookId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return false;
        }
        
        return reviewRepository.existsByBookIdAndUserId(bookId, currentUserId);
    }

    /**
     * Calculate and get average rating for a book.
     *
     * @param bookId book ID
     * @return average rating or 0.0 if no reviews
     */
    @Transactional(readOnly = true)
    public double getAverageRating(final Long bookId) {
        log.debug("Calculating average rating for book ID: {}", bookId);
        Double average = reviewRepository.getAverageRatingByBookId(bookId);
        return average != null ? average : 0.0;
    }

    /**
     * Update book's average rating and total reviews count.
     *
     * @param bookId book ID
     */
    private void updateBookRating(final Long bookId) {
        log.debug("Updating book rating for book ID: {}", bookId);
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));
        
        book.updateAverageRating();
        bookRepository.save(book);
        
        log.debug("Book rating updated - Average: {}, Total Reviews: {}", 
                 book.getAverageRating(), book.getTotalReviews());
    }

    /**
     * Map Review entity to ReviewDTO.
     *
     * @param review the review entity
     * @return review DTO
     */
    private ReviewDTO mapToDTO(final Review review) {
        return ReviewDTO.builder()
                .id(review.getId())
                .bookId(review.getBook().getId())
                .bookTitle(review.getBook().getTitle())
                .userId(review.getUser().getId())
                .userFirstName(review.getUser().getFirstName())
                .userLastName(review.getUser().getLastName())
                .rating(review.getRating())
                .text(review.getText())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}