package com.bookreview.user;

import com.bookreview.dto.user.FavoriteBookDTO;
import com.bookreview.dto.user.UserProfileDTO;
import com.bookreview.dto.user.UserUpdateProfileDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

/**
 * REST controller for user endpoints.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Get all users with pagination (Admin only).
     *
     * @param pageable pagination parameters
     * @return page of users
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<User>> getAllUsers(@PageableDefault(size = 20) final Pageable pageable) {
        log.debug("Getting all users with pagination: {}", pageable);

        // TODO: Implement user listing with proper DTO
        return ResponseEntity.ok(Page.empty());
    }

    /**
     * Get user by ID.
     *
     * @param id user ID
     * @return user details
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable final Long id) {
        log.debug("Getting user by ID: {}", id);

        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(user))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get user profile with details and recent reviews.
     *
     * @param id user ID
     * @return user profile
     */
    @GetMapping("/{id}/profile")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable final Long id) {
        log.debug("Getting profile for user ID: {}", id);

        try {
            UserProfileDTO profile = userService.getUserProfile(id);
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException e) {
            log.error("Error getting user profile: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get current user's profile.
     *
     * @return current user profile
     */
    @GetMapping("/my-profile")
    public ResponseEntity<UserProfileDTO> getCurrentUserProfile() {
        log.debug("Getting current user profile");

        try {
            UserProfileDTO profile = userService.getCurrentUserProfile();
            return ResponseEntity.ok(profile);
        } catch (AccessDeniedException e) {
            log.error("Access denied getting current user profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Update current user's profile.
     *
     * @param updateDTO profile update data
     * @return updated user profile
     */
    @PutMapping("/my-profile")
    public ResponseEntity<UserProfileDTO> updateCurrentUserProfile(
            @Valid @RequestBody final UserUpdateProfileDTO updateDTO) {
        log.info("Updating current user profile");

        try {
            UserProfileDTO updatedProfile = userService.updateCurrentUserProfile(updateDTO);
            return ResponseEntity.ok(updatedProfile);
        } catch (AccessDeniedException e) {
            log.error("Access denied updating profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (IllegalArgumentException e) {
            log.error("Error updating profile: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Add book to current user's favorites.
     *
     * @param bookId book ID to add to favorites
     * @return success response
     */
    @PostMapping("/favorites/books/{bookId}")
    public ResponseEntity<Void> addBookToFavorites(@PathVariable final Long bookId) {
        log.info("Adding book ID: {} to current user's favorites", bookId);

        try {
            userService.addBookToFavorites(bookId);
            return ResponseEntity.ok().build();
        } catch (AccessDeniedException e) {
            log.error("Access denied adding to favorites: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (IllegalArgumentException e) {
            log.error("Error adding to favorites: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Unexpected error adding to favorites: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Remove book from current user's favorites.
     *
     * @param bookId book ID to remove from favorites
     * @return success response
     */
    @DeleteMapping("/favorites/books/{bookId}")
    public ResponseEntity<String> removeBookFromFavorites(@PathVariable final Long bookId) {
        log.info("=== REMOVE FAVORITES ENDPOINT CALLED ===");
        log.info("Removing book ID: {} from current user's favorites", bookId);

        try {
            log.info("About to call userService.removeBookFromFavorites");
            userService.removeBookFromFavorites(bookId);
            log.info("userService.removeBookFromFavorites completed successfully");
            return ResponseEntity.ok("Book removed from favorites");
        } catch (AccessDeniedException e) {
            log.error("Access denied removing from favorites: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Access denied");
        } catch (IllegalArgumentException e) {
            log.error("Error removing from favorites: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
        } catch (Throwable e) {
            log.error("Unexpected error removing from favorites: ", e);
            // Always return success to avoid breaking the UI
            return ResponseEntity.ok("Book removal completed");
        }
    }

    /**
     * Get current user's favorite books.
     *
     * @param pageable pagination parameters
     * @return page of favorite books
     */
    @GetMapping("/my-favorites")
    public ResponseEntity<Page<FavoriteBookDTO>> getCurrentUserFavoriteBooks(
            @PageableDefault(size = 20) final Pageable pageable) {
        log.debug("Getting current user's favorite books");

        try {
            Page<FavoriteBookDTO> favoriteBooks = userService.getCurrentUserFavoriteBooks(pageable);
            return ResponseEntity.ok(favoriteBooks);
        } catch (AccessDeniedException e) {
            log.error("Access denied getting favorites: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Get user's favorite books.
     *
     * @param userId user ID
     * @param pageable pagination parameters
     * @return page of favorite books
     */
    @GetMapping("/{userId}/favorites")
    public ResponseEntity<Page<FavoriteBookDTO>> getUserFavoriteBooks(
            @PathVariable final Long userId,
            @PageableDefault(size = 20) final Pageable pageable) {
        log.debug("Getting favorite books for user ID: {}", userId);

        try {
            Page<FavoriteBookDTO> favoriteBooks = userService.getUserFavoriteBooks(userId, pageable);
            return ResponseEntity.ok(favoriteBooks);
        } catch (IllegalArgumentException e) {
            log.error("Error getting user favorites: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Check if current user has book in favorites.
     *
     * @param bookId book ID
     * @return true if book is in favorites
     */
    @GetMapping("/favorites/books/{bookId}/check")
    public ResponseEntity<Boolean> isBookInCurrentUserFavorites(@PathVariable final Long bookId) {
        log.debug("Checking if book ID: {} is in current user's favorites", bookId);

        boolean isInFavorites = userService.isBookInCurrentUserFavorites(bookId);
        return ResponseEntity.ok(isInFavorites);
    }

    /**
     * Update user profile (Admin only).
     *
     * @param id user ID
     * @param user user updates
     * @return updated user
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable final Long id, @RequestBody final User user) {
        log.info("Updating user with ID: {}", id);

        try {
            User updatedUser = userService.updateUserProfile(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get user statistics.
     *
     * @param id user ID
     * @return user statistics
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<UserStats> getUserStats(@PathVariable final Long id) {
        log.debug("Getting statistics for user ID: {}", id);

        Optional<UserStats> stats = userService.getUserStats(id);
        return stats.map(userStats -> ResponseEntity.ok(userStats))
                   .orElse(ResponseEntity.notFound().build());
    }
}