package com.bookreview.review;

import com.bookreview.dto.review.ReviewCreateDTO;
import com.bookreview.dto.review.ReviewDTO;
import com.bookreview.dto.review.ReviewUpdateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for review endpoints.
 */
@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Get all reviews with pagination and filtering.
     *
     * @param pageable pagination parameters
     * @param bookId filter by book ID
     * @param userId filter by user ID
     * @param rating filter by rating
     * @param minRating minimum rating filter
     * @return page of reviews
     */
    @GetMapping
    public ResponseEntity<Page<ReviewDTO>> getAllReviews(
            @PageableDefault(size = 20) final Pageable pageable,
            @RequestParam(required = false) final Long bookId,
            @RequestParam(required = false) final Long userId,
            @RequestParam(required = false) final Integer rating,
            @RequestParam(required = false) final Integer minRating) {
        
        log.debug("Getting reviews with filters - bookId: {}, userId: {}, rating: {}", bookId, userId, rating);

        Page<ReviewDTO> reviews;
        if (bookId != null) {
            reviews = reviewService.getReviewsByBookId(bookId, pageable);
        } else if (userId != null) {
            reviews = reviewService.getReviewsByUserId(userId, pageable);
        } else {
            reviews = reviewService.getAllReviews(pageable);
        }

        return ResponseEntity.ok(reviews);
    }

    /**
     * Get review by ID.
     *
     * @param id review ID
     * @return review details
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable final Long id) {
        log.debug("Getting review by ID: {}", id);

        return reviewService.getReviewById(id)
                .map(review -> ResponseEntity.ok(review))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new review.
     *
     * @param createDTO review creation data
     * @return created review
     */
    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(@Valid @RequestBody final ReviewCreateDTO createDTO) {
        log.info("Creating new review for book ID: {}", createDTO.getBookId());

        try {
            ReviewDTO createdReview = reviewService.createReview(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
        } catch (IllegalArgumentException e) {
            log.error("Error creating review: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (AccessDeniedException e) {
            log.error("Access denied creating review: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Update an existing review.
     *
     * @param id review ID
     * @param updateDTO review update data
     * @return updated review
     */
    @PutMapping("/{id}")
    public ResponseEntity<ReviewDTO> updateReview(@PathVariable final Long id, 
                                                 @Valid @RequestBody final ReviewUpdateDTO updateDTO) {
        log.info("Updating review with ID: {}", id);

        try {
            ReviewDTO updatedReview = reviewService.updateReview(id, updateDTO);
            return ResponseEntity.ok(updatedReview);
        } catch (IllegalArgumentException e) {
            log.error("Error updating review: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException e) {
            log.error("Access denied updating review: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Delete a review.
     *
     * @param id review ID
     * @return success response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable final Long id) {
        log.info("Deleting review with ID: {}", id);

        try {
            reviewService.deleteReview(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Error deleting review: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException e) {
            log.error("Access denied deleting review: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Get reviews by book ID.
     *
     * @param bookId book ID
     * @param pageable pagination parameters
     * @return page of reviews
     */
    @GetMapping("/book/{bookId}")
    public ResponseEntity<Page<ReviewDTO>> getReviewsByBookId(
            @PathVariable final Long bookId,
            @PageableDefault(size = 20) final Pageable pageable) {
        
        log.debug("Getting reviews for book ID: {}", bookId);

        try {
            Page<ReviewDTO> reviews = reviewService.getReviewsByBookId(bookId, pageable);
            return ResponseEntity.ok(reviews);
        } catch (IllegalArgumentException e) {
            log.error("Error getting reviews for book: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get reviews by user ID.
     *
     * @param userId user ID
     * @param pageable pagination parameters
     * @return page of reviews
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ReviewDTO>> getReviewsByUserId(
            @PathVariable final Long userId,
            @PageableDefault(size = 20) final Pageable pageable) {
        
        log.debug("Getting reviews for user ID: {}", userId);

        try {
            Page<ReviewDTO> reviews = reviewService.getReviewsByUserId(userId, pageable);
            return ResponseEntity.ok(reviews);
        } catch (IllegalArgumentException e) {
            log.error("Error getting reviews for user: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get current user's reviews.
     *
     * @param pageable pagination parameters
     * @return page of reviews
     */
    @GetMapping("/my-reviews")
    public ResponseEntity<Page<ReviewDTO>> getCurrentUserReviews(
            @PageableDefault(size = 20) final Pageable pageable) {
        
        log.debug("Getting reviews for current user");

        try {
            Page<ReviewDTO> reviews = reviewService.getCurrentUserReviews(pageable);
            return ResponseEntity.ok(reviews);
        } catch (AccessDeniedException e) {
            log.error("Access denied getting current user reviews: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Get current user's review for a specific book.
     *
     * @param bookId book ID
     * @return review details or 404 if not found
     */
    @GetMapping("/my-review/book/{bookId}")
    public ResponseEntity<ReviewDTO> getCurrentUserReviewForBook(@PathVariable final Long bookId) {
        log.debug("Getting current user's review for book ID: {}", bookId);

        return reviewService.getCurrentUserReviewForBook(bookId)
                .map(review -> ResponseEntity.ok(review))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Check if current user has reviewed a book.
     *
     * @param bookId book ID
     * @return true if user has reviewed the book
     */
    @GetMapping("/has-reviewed/book/{bookId}")
    public ResponseEntity<Boolean> hasCurrentUserReviewedBook(@PathVariable final Long bookId) {
        log.debug("Checking if current user has reviewed book ID: {}", bookId);

        boolean hasReviewed = reviewService.hasCurrentUserReviewedBook(bookId);
        return ResponseEntity.ok(hasReviewed);
    }
}