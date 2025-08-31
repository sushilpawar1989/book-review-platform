package com.bookreview.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repository interface for Book entity operations.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Find books by title containing search term (case insensitive).
     *
     * @param title the title search term
     * @param pageable pagination information
     * @return page of books
     */
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    /**
     * Find books by author containing search term (case insensitive).
     *
     * @param author the author search term
     * @param pageable pagination information
     * @return page of books
     */
    Page<Book> findByAuthorContainingIgnoreCase(String author, Pageable pageable);

    /**
     * Find books by genre.
     *
     * @param genre the genre
     * @param pageable pagination information
     * @return page of books
     */
    @Query("SELECT b FROM Book b JOIN b.genres g WHERE g = :genre")
    Page<Book> findByGenre(@Param("genre") Genre genre, Pageable pageable);

    /**
     * Find books by multiple genres.
     *
     * @param genres the genres
     * @param pageable pagination information
     * @return page of books
     */
    @Query("SELECT DISTINCT b FROM Book b JOIN b.genres g WHERE g IN :genres")
    Page<Book> findByGenresIn(@Param("genres") Set<Genre> genres, Pageable pageable);

    /**
     * Find books by published year.
     *
     * @param publishedYear the published year
     * @param pageable pagination information
     * @return page of books
     */
    Page<Book> findByPublishedYear(Integer publishedYear, Pageable pageable);

    /**
     * Find books published between years.
     *
     * @param startYear start year
     * @param endYear end year
     * @param pageable pagination information
     * @return page of books
     */
    Page<Book> findByPublishedYearBetween(Integer startYear, Integer endYear, Pageable pageable);

    /**
     * Search books by title, author, or description.
     *
     * @param searchTerm the search term
     * @param pageable pagination information
     * @return page of books
     */
    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.author) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Book> searchBooks(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Search books by title and/or author.
     *
     * @param title title search term
     * @param author author search term
     * @param pageable pagination information
     * @return page of books
     */
    @Query("SELECT b FROM Book b WHERE " +
           "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:author IS NULL OR LOWER(b.author) LIKE LOWER(CONCAT('%', :author, '%')))")
    Page<Book> findByTitleAndAuthor(@Param("title") String title, 
                                   @Param("author") String author, 
                                   Pageable pageable);

    /**
     * Find books with rating between min and max.
     *
     * @param minRating minimum rating
     * @param maxRating maximum rating
     * @param pageable pagination information
     * @return page of books
     */
    Page<Book> findByAverageRatingBetween(BigDecimal minRating, BigDecimal maxRating, Pageable pageable);

    /**
     * Find books with minimum rating and minimum reviews.
     *
     * @param minRating minimum rating
     * @param minReviews minimum number of reviews
     * @param pageable pagination information
     * @return page of books
     */
    @Query("SELECT b FROM Book b WHERE b.averageRating >= :minRating AND b.totalReviews >= :minReviews")
    Page<Book> findTopRatedBooks(@Param("minRating") BigDecimal minRating, 
                                 @Param("minReviews") int minReviews, 
                                 Pageable pageable);




    /**
     * Find recently added books.
     *
     * @param sinceDate date to look back from
     * @param pageable pagination information
     * @return page of recently added books
     */
    @Query("SELECT b FROM Book b WHERE b.createdAt >= :sinceDate")
    Page<Book> findRecentlyAdded(@Param("sinceDate") java.time.LocalDateTime sinceDate, Pageable pageable);

    /**
     * Find books by similar genres to user preferences.
     *
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of books
     */
    @Query("SELECT DISTINCT b FROM Book b JOIN b.genres bg WHERE bg IN " +
           "(SELECT pg FROM User u JOIN u.preferredGenres pg WHERE u.id = :userId) " +
           "AND b.id NOT IN " +
           "(SELECT r.book.id FROM Review r WHERE r.user.id = :userId)")
    Page<Book> findBooksByUserGenrePreferences(@Param("userId") Long userId, Pageable pageable);

    /**
     * Count books by genre.
     *
     * @param genre the genre
     * @return count of books
     */
    @Query("SELECT COUNT(DISTINCT b) FROM Book b JOIN b.genres g WHERE g = :genre")
    long countByGenre(@Param("genre") Genre genre);

    /**
     * Check if book exists by title.
     *
     * @param title the book title
     * @return true if book exists
     */
    boolean existsByTitle(String title);

    /**
     * Find books favorited by users with similar preferences.
     *
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of recommended books
     */
    @Query("SELECT DISTINCT b FROM Book b JOIN b.favoritedByUsers u1 " +
           "WHERE u1.id IN " +
           "(SELECT DISTINCT u2.id FROM User u2 JOIN u2.favoriteBooks fb " +
           "WHERE fb.id IN (SELECT ufb.id FROM User u JOIN u.favoriteBooks ufb WHERE u.id = :userId) " +
           "AND u2.id != :userId) " +
           "AND b.id NOT IN " +
           "(SELECT ufb2.id FROM User u3 JOIN u3.favoriteBooks ufb2 WHERE u3.id = :userId)")
    Page<Book> findBooksByFavoriteSimilarity(@Param("userId") Long userId, Pageable pageable);
}
