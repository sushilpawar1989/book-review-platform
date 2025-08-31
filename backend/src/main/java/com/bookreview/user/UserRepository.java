package com.bookreview.user;

import com.bookreview.book.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repository interface for User entity operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email address.
     *
     * @param email the email address
     * @return optional user
     */
    Optional<User> findByEmail(String email);

    /**
     * Find favorite books for a user.
     *
     * @param userId user ID
     * @param pageable pagination information
     * @return page of favorite books
     */
    @Query("SELECT b FROM User u JOIN u.favoriteBooks b WHERE u.id = :userId ORDER BY b.title")
    Page<com.bookreview.book.Book> findFavoriteBooksByUserId(@Param("userId") Long userId, Pageable pageable);

    /**
     * Check if user has book in favorites.
     *
     * @param userId user ID
     * @param bookId book ID
     * @return true if book is in user's favorites
     */
    @Query("SELECT COUNT(b) > 0 FROM User u JOIN u.favoriteBooks b WHERE u.id = :userId AND b.id = :bookId")
    boolean existsFavoriteBookByUserIdAndBookId(@Param("userId") Long userId, @Param("bookId") Long bookId);

    /**
     * Add book to user's favorites using native SQL.
     *
     * @param userId user ID
     * @param bookId book ID
     */
    @Modifying
    @Query(value = "INSERT INTO user_favorite_books (user_id, book_id) VALUES (:userId, :bookId)", nativeQuery = true)
    void addBookToUserFavorites(@Param("userId") Long userId, @Param("bookId") Long bookId);

    /**
     * Remove book from user's favorites using native SQL.
     *
     * @param userId user ID
     * @param bookId book ID
     */
    @Modifying
    @Query(value = "DELETE FROM user_favorite_books WHERE user_id = :userId AND book_id = :bookId", nativeQuery = true)
    void removeBookFromUserFavorites(@Param("userId") Long userId, @Param("bookId") Long bookId);

    /**
     * Check if user exists by email address.
     *
     * @param email the email address
     * @return true if user exists
     */
    boolean existsByEmail(String email);

    /**
     * Find users by role.
     *
     * @param role the user role
     * @param pageable pagination information
     * @return page of users
     */
    Page<User> findByRole(UserRole role, Pageable pageable);

    /**
     * Find users by preferred genres.
     *
     * @param genres the preferred genres
     * @param pageable pagination information
     * @return page of users
     */
    @Query("SELECT DISTINCT u FROM User u JOIN u.preferredGenres pg WHERE pg IN :genres")
    Page<User> findByPreferredGenresIn(@Param("genres") Set<Genre> genres, Pageable pageable);

    /**
     * Find users created after specified date.
     *
     * @param date the date
     * @param pageable pagination information
     * @return page of users
     */
    Page<User> findByCreatedAtAfter(LocalDateTime date, Pageable pageable);

    /**
     * Find users who have written reviews.
     *
     * @param pageable pagination information
     * @return page of users
     */
    @Query("SELECT DISTINCT u FROM User u WHERE SIZE(u.reviews) > 0")
    Page<User> findUsersWithReviews(Pageable pageable);

    /**
     * Find users with favorite books.
     *
     * @param pageable pagination information
     * @return page of users
     */
    @Query("SELECT DISTINCT u FROM User u WHERE SIZE(u.favoriteBooks) > 0")
    Page<User> findUsersWithFavorites(Pageable pageable);

    /**
     * Get user statistics.
     *
     * @param userId the user ID
     * @return user statistics
     */
    @Query("SELECT new com.bookreview.user.UserStats(" +
           "u.id, " +
           "SIZE(u.reviews), " +
           "SIZE(u.favoriteBooks), " +
           "COALESCE(AVG(CAST(r.rating AS double)), 0.0)) " +
           "FROM User u LEFT JOIN u.reviews r WHERE u.id = :userId GROUP BY u.id")
    Optional<UserStats> getUserStats(@Param("userId") Long userId);

    /**
     * Find users by name containing search term.
     *
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return page of users
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> findByNameContaining(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find active users (users who have activity within specified days).
     *
     * @param days number of days to look back
     * @param pageable pagination information
     * @return page of active users
     */
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN u.reviews r " +
           "WHERE u.createdAt >= :sinceDate OR r.createdAt >= :sinceDate")
    Page<User> findActiveUsers(@Param("sinceDate") LocalDateTime sinceDate, Pageable pageable);

    /**
     * Count users by role.
     *
     * @param role the user role
     * @return count of users
     */
    long countByRole(UserRole role);

    /**
     * Find users who favorited a specific book.
     *
     * @param bookId the book ID
     * @return list of users
     */
    @Query("SELECT u FROM User u JOIN u.favoriteBooks b WHERE b.id = :bookId")
    List<User> findUsersByFavoriteBook(@Param("bookId") Long bookId);
}
