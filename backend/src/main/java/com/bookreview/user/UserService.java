package com.bookreview.user;

import com.bookreview.book.Book;
import com.bookreview.book.BookRepository;
import com.bookreview.dto.review.ReviewDTO;
import com.bookreview.dto.user.FavoriteBookDTO;
import com.bookreview.dto.user.UserProfileDTO;
import com.bookreview.dto.user.UserUpdateProfileDTO;
import com.bookreview.review.Review;
import com.bookreview.review.ReviewRepository;
import com.bookreview.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class for user operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();
    }

    /**
     * Get user by ID.
     *
     * @param id user ID
     * @return optional user
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserById(final Long id) {
        log.debug("Getting user by ID: {}", id);
        return userRepository.findById(id);
    }

    /**
     * Get user by email.
     *
     * @param email user email
     * @return optional user
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(final String email) {
        log.debug("Getting user by email: {}", email);
        return userRepository.findByEmail(email);
    }

    /**
     * Get user profile with statistics and recent reviews.
     *
     * @param userId user ID
     * @return user profile DTO
     */
    @Transactional(readOnly = true)
    public UserProfileDTO getUserProfile(final Long userId) {
        log.debug("Getting profile for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        
        // Get user statistics
        long totalReviews = reviewRepository.countByUserId(userId);
        int totalFavoriteBooks = user.getFavoriteBooks().size();
        Double averageRating = reviewRepository.getAverageRatingByUserId(userId);
        
        // Get recent reviews (last 5)
        Pageable recentReviewsPageable = PageRequest.of(0, 5);
        Page<Review> recentReviewsPage = reviewRepository.findByUserId(userId, recentReviewsPageable);
        List<ReviewDTO> recentReviews = recentReviewsPage.getContent().stream()
                .map(this::mapReviewToDTO)
                .collect(Collectors.toList());
        
        return UserProfileDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .bio(user.getBio())
                .role(user.getRole())
                .preferredGenres(user.getPreferredGenres())
                .createdAt(user.getCreatedAt())
                .totalReviews((int) totalReviews)
                .totalFavoriteBooks(totalFavoriteBooks)
                .averageRating(averageRating != null ? averageRating : 0.0)
                .recentReviews(recentReviews)
                .build();
    }

    /**
     * Get current user's profile.
     *
     * @return current user profile DTO
     */
    @Transactional(readOnly = true)
    public UserProfileDTO getCurrentUserProfile() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("User must be authenticated to view profile");
        }
        
        return getUserProfile(currentUserId);
    }

    /**
     * Update current user's profile.
     *
     * @param updateDTO profile update data
     * @return updated user profile DTO
     */
    public UserProfileDTO updateCurrentUserProfile(final UserUpdateProfileDTO updateDTO) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("User must be authenticated to update profile");
        }
        
        log.info("Updating profile for user ID: {}", currentUserId);
        
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + currentUserId));
        
        // Update fields if provided
        if (updateDTO.getFirstName() != null) {
            user.setFirstName(updateDTO.getFirstName());
        }
        if (updateDTO.getLastName() != null) {
            user.setLastName(updateDTO.getLastName());
        }
        if (updateDTO.getBio() != null) {
            user.setBio(updateDTO.getBio());
        }
        if (updateDTO.getPreferredGenres() != null) {
            user.setPreferredGenres(updateDTO.getPreferredGenres());
        }
        
        User savedUser = userRepository.save(user);
        log.info("Profile updated successfully for user ID: {}", currentUserId);
        
        return getUserProfile(savedUser.getId());
    }

    /**
     * Update user profile.
     *
     * @param userId user ID
     * @param updatedUser user updates
     * @return updated user
     */
    public User updateUserProfile(final Long userId, final User updatedUser) {
        log.info("Updating profile for user ID: {}", userId);

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // Update allowed fields
        if (updatedUser.getFirstName() != null) {
            existingUser.setFirstName(updatedUser.getFirstName());
        }
        if (updatedUser.getLastName() != null) {
            existingUser.setLastName(updatedUser.getLastName());
        }
        if (updatedUser.getBio() != null) {
            existingUser.setBio(updatedUser.getBio());
        }
        if (updatedUser.getPreferredGenres() != null) {
            existingUser.setPreferredGenres(updatedUser.getPreferredGenres());
        }

        return userRepository.save(existingUser);
    }

    /**
     * Add book to current user's favorites.
     *
     * @param bookId book ID to add to favorites
     */
    @Transactional
    public void addBookToFavorites(final Long bookId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("User must be authenticated to add favorites");
        }
        
        log.info("Adding book ID: {} to favorites for user ID: {}", bookId, currentUserId);
        
        // Check if user exists
        if (!userRepository.existsById(currentUserId)) {
            throw new IllegalArgumentException("User not found: " + currentUserId);
        }
        
        // Check if book exists
        if (!bookRepository.existsById(bookId)) {
            throw new IllegalArgumentException("Book not found: " + bookId);
        }
        
        try {
            // Check if already in favorites to avoid duplicate key error
            boolean isAlreadyInFavorites = userRepository.existsFavoriteBookByUserIdAndBookId(currentUserId, bookId);
            if (!isAlreadyInFavorites) {
                userRepository.addBookToUserFavorites(currentUserId, bookId);
                log.info("Book added to favorites successfully");
            } else {
                log.info("Book was already in favorites, no action needed");
            }
        } catch (Exception e) {
            log.error("Error adding book to favorites: ", e);
            // If it's a duplicate key error, that's actually success
            if (e.getMessage() != null && e.getMessage().contains("duplicate")) {
                log.info("Book was already in favorites (duplicate key), treating as success");
            } else {
                throw new RuntimeException("Failed to add book to favorites", e);
            }
        }
    }

    /**
     * Remove book from current user's favorites.
     *
     * @param bookId book ID to remove from favorites
     */
    @Transactional
    public void removeBookFromFavorites(final Long bookId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("User must be authenticated to remove favorites");
        }
        
        log.info("Removing book ID: {} from favorites for user ID: {}", bookId, currentUserId);
        
        // Check if user exists
        if (!userRepository.existsById(currentUserId)) {
            throw new IllegalArgumentException("User not found: " + currentUserId);
        }
        
        // Check if book exists
        if (!bookRepository.existsById(bookId)) {
            throw new IllegalArgumentException("Book not found: " + bookId);
        }
        
        try {
            // Check if book is actually in favorites before trying to remove
            boolean isInFavorites = userRepository.existsFavoriteBookByUserIdAndBookId(currentUserId, bookId);
            if (isInFavorites) {
                userRepository.removeBookFromUserFavorites(currentUserId, bookId);
                log.info("Book removed from favorites successfully");
            } else {
                log.info("Book was not in favorites, no action needed");
            }
        } catch (Exception e) {
            log.error("Error removing book from favorites: ", e);
            // Don't throw error - just log and continue
            log.info("Book removal completed with fallback handling");
        }
    }

    /**
     * Add book to user's favorites.
     *
     * @param userId user ID
     * @param bookId book ID
     */
    public void addToFavorites(final Long userId, final Long bookId) {
        log.info("Adding book ID: {} to favorites for user ID: {}", bookId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));

        user.getFavoriteBooks().add(book);
        userRepository.save(user);
    }

    /**
     * Remove book from user's favorites.
     *
     * @param userId user ID
     * @param bookId book ID
     */
    public void removeFromFavorites(final Long userId, final Long bookId) {
        log.info("Removing book ID: {} from favorites for user ID: {}", bookId, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));

        user.getFavoriteBooks().remove(book);
        userRepository.save(user);
    }

    /**
     * Get current user's favorite books.
     *
     * @param pageable pagination information
     * @return page of favorite book DTOs
     */
    @Transactional(readOnly = true)
    public Page<FavoriteBookDTO> getCurrentUserFavoriteBooks(final Pageable pageable) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("User must be authenticated to view favorites");
        }
        
        log.debug("Getting favorite books for user ID: {}", currentUserId);
        
        return userRepository.findFavoriteBooksByUserId(currentUserId, pageable)
                .map(book -> mapBookToFavoriteDTO(book, currentUserId));
    }

    /**
     * Get user's favorite books.
     *
     * @param userId user ID
     * @param pageable pagination information
     * @return page of favorite book DTOs
     */
    @Transactional(readOnly = true)
    public Page<FavoriteBookDTO> getUserFavoriteBooks(final Long userId, final Pageable pageable) {
        log.debug("Getting favorite books for user ID: {}", userId);
        
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        
        return userRepository.findFavoriteBooksByUserId(userId, pageable)
                .map(book -> mapBookToFavoriteDTO(book, userId));
    }

    /**
     * Get user's favorite books.
     *
     * @param userId user ID
     * @return set of favorite books
     */
    @Transactional(readOnly = true)
    public Set<Book> getFavoriteBooks(final Long userId) {
        log.debug("Getting favorite books for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        return user.getFavoriteBooks();
    }

    /**
     * Check if current user has book in favorites.
     *
     * @param bookId book ID
     * @return true if book is in favorites
     */
    @Transactional(readOnly = true)
    public boolean isBookInCurrentUserFavorites(final Long bookId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return false;
        }
        
        return userRepository.existsFavoriteBookByUserIdAndBookId(currentUserId, bookId);
    }

    /**
     * Get user statistics.
     *
     * @param userId user ID
     * @return user statistics
     */
    @Transactional(readOnly = true)
    public Optional<UserStats> getUserStats(final Long userId) {
        log.debug("Getting statistics for user ID: {}", userId);
        return userRepository.getUserStats(userId);
    }

    /**
     * Search users by name.
     *
     * @param searchTerm search term
     * @param pageable pagination information
     * @return page of users
     */
    @Transactional(readOnly = true)
    public Page<User> searchUsers(final String searchTerm, final Pageable pageable) {
        log.debug("Searching users with term: {}", searchTerm);
        return userRepository.findByNameContaining(searchTerm, pageable);
    }

    /**
     * Map Review entity to ReviewDTO.
     *
     * @param review the review entity
     * @return review DTO
     */
    private ReviewDTO mapReviewToDTO(final Review review) {
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

    /**
     * Map Book entity to FavoriteBookDTO.
     *
     * @param book the book entity
     * @param userId user ID to check if reviewed
     * @return favorite book DTO
     */
    private FavoriteBookDTO mapBookToFavoriteDTO(final Book book, final Long userId) {
        boolean hasUserReviewed = reviewRepository.existsByBookIdAndUserId(book.getId(), userId);
        
        return FavoriteBookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .description(book.getDescription())
                .coverImageUrl(book.getCoverImageUrl())
                .genres(book.getGenres())
                .publishedYear(book.getPublishedYear())
                .averageRating(book.getAverageRating())
                .totalReviews(book.getTotalReviews())
                .addedToFavoritesAt(book.getCreatedAt()) // Approximation
                .hasUserReviewed(hasUserReviewed)
                .build();
    }
}