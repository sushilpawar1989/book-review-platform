package com.bookreview.recommendation;

import com.bookreview.book.Book;
import com.bookreview.book.BookRepository;
import com.bookreview.book.Genre;
import com.bookreview.dto.book.BookDTO;
import com.bookreview.dto.recommendation.RecommendationDTO;
import com.bookreview.dto.recommendation.RecommendationRequestDTO;
import com.bookreview.review.ReviewRepository;
import com.bookreview.service.OpenAIRecommendationService;
import com.bookreview.user.User;
import com.bookreview.user.UserRepository;
import com.bookreview.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for generating book recommendations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final OpenAIRecommendationService openAIService;

    /**
     * Get personalized recommendations for the current user.
     *
     * @param requestDTO recommendation request parameters
     * @return list of recommendations
     */
    @Transactional(readOnly = true)
    public List<RecommendationDTO> getRecommendationsForCurrentUser(final RecommendationRequestDTO requestDTO) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            throw new AccessDeniedException("User must be authenticated to get recommendations");
        }

        return getRecommendationsForUser(currentUserId, requestDTO);
    }

    /**
     * Get personalized recommendations for a specific user.
     *
     * @param userId user ID
     * @param requestDTO recommendation request parameters
     * @return list of recommendations
     */
    @Transactional(readOnly = true)
    public List<RecommendationDTO> getRecommendationsForUser(final Long userId, final RecommendationRequestDTO requestDTO) {
        log.info("Getting recommendations for user: {} with parameters: {}", userId, requestDTO);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        List<RecommendationDTO> recommendations = new ArrayList<>();
        Set<Long> recommendedBookIds = new HashSet<>();

        // Get books the user has already reviewed to exclude them
        Set<Long> reviewedBookIds = getReviewedBookIdsByUser(userId);

        // Strategy 1: Top-rated books
        if (requestDTO.getIncludeTopRated()) {
            List<RecommendationDTO> topRatedRecs = getTopRatedRecommendations(
                    user, reviewedBookIds, requestDTO.getMinRating(), requestDTO.getMinReviews(), 
                    Math.min(5, requestDTO.getLimit())
            );
            recommendations.addAll(topRatedRecs);
            recommendedBookIds.addAll(topRatedRecs.stream().map(r -> r.getBook().getId()).collect(Collectors.toSet()));
        }

        // Strategy 2: Genre-based recommendations
        if (requestDTO.getIncludeGenreBased() && recommendations.size() < requestDTO.getLimit()) {
            List<RecommendationDTO> genreRecs = getGenreBasedRecommendations(
                    user, reviewedBookIds, recommendedBookIds, requestDTO.getMinRating(), 
                    requestDTO.getMinReviews(), requestDTO.getLimit() - recommendations.size()
            );
            recommendations.addAll(genreRecs);
            recommendedBookIds.addAll(genreRecs.stream().map(r -> r.getBook().getId()).collect(Collectors.toSet()));
        }

        // Strategy 3: AI-powered recommendations (when enabled)
        if (requestDTO.getIncludeAIPowered() && openAIService.isAvailable() && recommendations.size() < requestDTO.getLimit()) {
            List<RecommendationDTO> aiRecs = getAIPoweredRecommendations(
                    user, reviewedBookIds, recommendedBookIds, requestDTO.getLimit() - recommendations.size()
            );
            recommendations.addAll(aiRecs);
        }

        // Limit results
        return recommendations.stream()
                .limit(requestDTO.getLimit())
                .toList();
    }

    /**
     * Get top-rated book recommendations.
     *
     * @param user the user
     * @param excludeBookIds book IDs to exclude
     * @param minRating minimum rating
     * @param minReviews minimum reviews
     * @param limit recommendation limit
     * @return list of top-rated recommendations
     */
    private List<RecommendationDTO> getTopRatedRecommendations(final User user, final Set<Long> excludeBookIds, 
                                                              final Double minRating, final Integer minReviews, final int limit) {
        log.debug("Getting top-rated recommendations for user: {}", user.getId());

        Pageable pageable = PageRequest.of(0, limit * 2); // Get more to account for exclusions
        Page<Book> topRatedBooks = bookRepository.findTopRatedBooks(BigDecimal.valueOf(minRating), minReviews, pageable);

        return topRatedBooks.getContent().stream()
                .filter(book -> !excludeBookIds.contains(book.getId()))
                .limit(limit)
                .map(book -> createRecommendation(user, book, RecommendationStrategy.TOP_RATED, 
                        "This book has an excellent rating of " + book.getAverageRating() + 
                        " based on " + book.getTotalReviews() + " reviews", 
                        calculateTopRatedScore(book)))
                .toList();
    }

    /**
     * Get genre-based book recommendations.
     *
     * @param user the user
     * @param excludeBookIds book IDs to exclude  
     * @param alreadyRecommended already recommended book IDs
     * @param minRating minimum rating
     * @param minReviews minimum reviews
     * @param limit recommendation limit
     * @return list of genre-based recommendations
     */
    private List<RecommendationDTO> getGenreBasedRecommendations(final User user, final Set<Long> excludeBookIds,
                                                               final Set<Long> alreadyRecommended, final Double minRating, 
                                                               final Integer minReviews, final int limit) {
        log.debug("Getting genre-based recommendations for user: {}", user.getId());

        List<RecommendationDTO> recommendations = new ArrayList<>();
        
        // Get genres from user's favorite books
        Set<Genre> favoriteGenres = getFavoriteGenresFromUserBooks(user);
        
        // Combine with user's preferred genres
        Set<Genre> allGenres = new HashSet<>(user.getPreferredGenres());
        allGenres.addAll(favoriteGenres);

        if (allGenres.isEmpty()) {
            log.debug("No preferred genres found for user: {}, using popular genres", user.getId());
            allGenres.addAll(Set.of(Genre.FICTION, Genre.MYSTERY, Genre.ROMANCE)); // Fallback to popular genres
        }

        for (Genre genre : allGenres) {
            if (recommendations.size() >= limit) break;

            Pageable pageable = PageRequest.of(0, limit);
            // Use the existing method to get books by genre
            Page<Book> genreBooks = bookRepository.findByGenre(genre, pageable);

            List<RecommendationDTO> genreRecs = genreBooks.getContent().stream()
                    .filter(book -> !excludeBookIds.contains(book.getId()))
                    .filter(book -> !alreadyRecommended.contains(book.getId()))
                    .filter(book -> book.getTotalReviews() >= minReviews)
                    .limit(limit - recommendations.size())
                    .map(book -> createRecommendation(user, book, RecommendationStrategy.GENRE_SIMILARITY,
                            "Based on your interest in " + genre.name().toLowerCase().replace('_', ' ') + " books",
                            calculateGenreBasedScore(book, genre, user)))
                    .toList();

            recommendations.addAll(genreRecs);
        }

        return recommendations;
    }

    /**
     * Get AI-powered book recommendations.
     *
     * @param user the user
     * @param excludeBookIds book IDs to exclude
     * @param alreadyRecommended already recommended book IDs
     * @param limit recommendation limit
     * @return list of AI-powered recommendations
     */
    private List<RecommendationDTO> getAIPoweredRecommendations(final User user, final Set<Long> excludeBookIds,
                                                              final Set<Long> alreadyRecommended, final int limit) {
        log.debug("Getting AI-powered recommendations for user: {}", user.getId());

        // Get books the user has read for context
        List<Book> userBooks = getUserReadBooks(user.getId());

        // Call OpenAI service
        List<Book> aiRecommendedBooks = openAIService.getAIRecommendations(user, userBooks, limit * 2);

        return aiRecommendedBooks.stream()
                .filter(book -> !excludeBookIds.contains(book.getId()))
                .filter(book -> !alreadyRecommended.contains(book.getId()))
                .limit(limit)
                .map(book -> createRecommendation(user, book, RecommendationStrategy.AI_POWERED,
                        "AI-powered recommendation based on your reading history and preferences",
                        0.9)) // High confidence for AI recommendations
                .toList();
    }

    /**
     * Get book IDs that the user has already reviewed.
     *
     * @param userId user ID
     * @return set of reviewed book IDs
     */
    private Set<Long> getReviewedBookIdsByUser(final Long userId) {
        return reviewRepository.findByUserId(userId, Pageable.unpaged())
                .map(review -> review.getBook().getId())
                .toSet();
    }

    /**
     * Get favorite genres from user's favorite books.
     *
     * @param user the user
     * @return set of genres from favorite books
     */
    private Set<Genre> getFavoriteGenresFromUserBooks(final User user) {
        return user.getFavoriteBooks().stream()
                .flatMap(book -> book.getGenres().stream())
                .collect(Collectors.toSet());
    }

    /**
     * Get books the user has read (reviewed).
     *
     * @param userId user ID
     * @return list of books the user has reviewed
     */
    private List<Book> getUserReadBooks(final Long userId) {
        return reviewRepository.findByUserId(userId, PageRequest.of(0, 50))
                .map(review -> review.getBook())
                .toList();
    }

    /**
     * Create a recommendation DTO.
     *
     * @param user the user
     * @param book the recommended book
     * @param strategy recommendation strategy
     * @param reason recommendation reason
     * @param score recommendation score
     * @return recommendation DTO
     */
    private RecommendationDTO createRecommendation(final User user, final Book book, 
                                                  final RecommendationStrategy strategy, 
                                                  final String reason, final Double score) {
        return RecommendationDTO.builder()
                .userId(user.getId())
                .book(mapBookToDTO(book))
                .strategy(strategy)
                .reason(reason)
                .score(score)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Calculate score for top-rated recommendations.
     *
     * @param book the book
     * @return recommendation score
     */
    private Double calculateTopRatedScore(final Book book) {
        // Score based on rating and number of reviews
        double ratingScore = book.getAverageRating().doubleValue() / 5.0; // Normalize to 0-1
        double reviewScore = Math.min(book.getTotalReviews() / 100.0, 1.0); // More reviews = higher score
        return (ratingScore * 0.7) + (reviewScore * 0.3);
    }

    /**
     * Calculate score for genre-based recommendations.
     *
     * @param book the book
     * @param genre the genre
     * @param user the user
     * @return recommendation score
     */
    private Double calculateGenreBasedScore(final Book book, final Genre genre, final User user) {
        double baseScore = book.getAverageRating().doubleValue() / 5.0;
        
        // Bonus for preferred genres
        double genreBonus = user.getPreferredGenres().contains(genre) ? 0.2 : 0.1;
        
        return Math.min(baseScore + genreBonus, 1.0);
    }

    /**
     * Map Book entity to BookDTO.
     *
     * @param book the book entity
     * @return book DTO
     */
    private BookDTO mapBookToDTO(final Book book) {
        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .description(book.getDescription())
                .coverImageUrl(book.getCoverImageUrl())
                .genres(book.getGenres())
                .publishedYear(book.getPublishedYear())
                .averageRating(book.getAverageRating())
                .totalReviews(book.getTotalReviews())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }
}