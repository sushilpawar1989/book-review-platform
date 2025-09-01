package com.bookreview.recommendation;

import com.bookreview.book.Book;
import com.bookreview.book.BookRepository;
import com.bookreview.book.Genre;
import com.bookreview.dto.recommendation.RecommendationDTO;
import com.bookreview.dto.recommendation.RecommendationRequestDTO;
import com.bookreview.review.Review;
import com.bookreview.review.ReviewRepository;
import com.bookreview.service.OpenAIRecommendationService;
import com.bookreview.user.User;
import com.bookreview.user.UserRepository;
import com.bookreview.user.UserRole;
import com.bookreview.util.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RecommendationRepository recommendationRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private OpenAIRecommendationService openAIService;

    @InjectMocks
    private RecommendationService recommendationService;

    private User user;
    private Book book1;
    private Book book2;
    private Review review;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .role(UserRole.USER)
                .preferredGenres(Set.of(Genre.FICTION))
                .favoriteBooks(new HashSet<>())
                .build();

        book1 = Book.builder()
                .id(1L)
                .title("Great Fiction Book")
                .author("Famous Author")
                .averageRating(BigDecimal.valueOf(4.5))
                .totalReviews(100)
                .genres(Set.of(Genre.FICTION))
                .publishedYear(2023)
                .createdAt(LocalDateTime.now())
                .build();

        book2 = Book.builder()
                .id(2L)
                .title("Another Great Book")
                .author("Another Author")
                .averageRating(BigDecimal.valueOf(4.8))
                .totalReviews(200)
                .genres(Set.of(Genre.MYSTERY))
                .publishedYear(2022)
                .createdAt(LocalDateTime.now())
                .build();

        review = Review.builder()
                .id(1L)
                .book(book1)
                .user(user)
                .rating(5)
                .text("Great book!")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getRecommendationsForCurrentUser_ShouldReturnRecommendations_WhenUserAuthenticated() {
        // Given
        RecommendationRequestDTO requestDTO = RecommendationRequestDTO.builder()
                .limit(5)
                .includeTopRated(true)
                .includeGenreBased(true)
                .includeAIPowered(false)
                .minRating(3.5)
                .minReviews(5)
                .build();

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(reviewRepository.findByUserId(eq(1L), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(review)));
            
            Page<Book> topRatedBooks = new PageImpl<>(List.of(book2));
            when(bookRepository.findTopRatedBooks(any(BigDecimal.class), any(Integer.class), any(Pageable.class)))
                    .thenReturn(topRatedBooks);
            
            Page<Book> genreBooks = new PageImpl<>(List.of(book1));
            when(bookRepository.findByGenre(any(Genre.class), any(Pageable.class)))
                    .thenReturn(genreBooks);

            // When
            List<RecommendationDTO> result = recommendationService.getRecommendationsForCurrentUser(requestDTO);

            // Then
            assertNotNull(result);
            assertFalse(result.isEmpty());
            assertTrue(result.stream().anyMatch(r -> r.getStrategy().equals(RecommendationStrategy.TOP_RATED)));
        }
    }

    @Test
    void getRecommendationsForCurrentUser_ShouldThrowException_WhenUserNotAuthenticated() {
        // Given
        RecommendationRequestDTO requestDTO = RecommendationRequestDTO.builder().build();

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(null);

            // When & Then
            assertThrows(AccessDeniedException.class, 
                        () -> recommendationService.getRecommendationsForCurrentUser(requestDTO));
        }
    }

    @Test
    void getRecommendationsForUser_ShouldReturnTopRatedRecommendations_WhenRequested() {
        // Given
        RecommendationRequestDTO requestDTO = RecommendationRequestDTO.builder()
                .limit(5)
                .includeTopRated(true)
                .includeGenreBased(false)
                .includeAIPowered(false)
                .minRating(4.0)
                .minReviews(10)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.findByUserId(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
        
        Page<Book> topRatedBooks = new PageImpl<>(List.of(book1, book2));
        when(bookRepository.findTopRatedBooks(any(BigDecimal.class), any(Integer.class), any(Pageable.class)))
                .thenReturn(topRatedBooks);

        // When
        List<RecommendationDTO> result = recommendationService.getRecommendationsForUser(1L, requestDTO);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(RecommendationStrategy.TOP_RATED, result.get(0).getStrategy());
        assertTrue(result.get(0).getReason().contains("excellent rating"));
    }

    @Test
    void getRecommendationsForUser_ShouldReturnGenreBasedRecommendations_WhenRequested() {
        // Given
        RecommendationRequestDTO requestDTO = RecommendationRequestDTO.builder()
                .limit(5)
                .includeTopRated(false)
                .includeGenreBased(true)
                .includeAIPowered(false)
                .minRating(3.5)
                .minReviews(5)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.findByUserId(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
        
        Page<Book> genreBooks = new PageImpl<>(List.of(book1));
        when(bookRepository.findByGenre(any(Genre.class), any(Pageable.class)))
                .thenReturn(genreBooks);

        // When
        List<RecommendationDTO> result = recommendationService.getRecommendationsForUser(1L, requestDTO);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(RecommendationStrategy.GENRE_SIMILARITY, result.get(0).getStrategy());
        assertTrue(result.get(0).getReason().contains("interest in"));
    }

    @Test
    void getRecommendationsForUser_ShouldReturnAIRecommendations_WhenEnabledAndRequested() {
        // Given
        RecommendationRequestDTO requestDTO = RecommendationRequestDTO.builder()
                .limit(5)
                .includeTopRated(false)
                .includeGenreBased(false)
                .includeAIPowered(true)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.findByUserId(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
        when(openAIService.isAvailable()).thenReturn(true);
        when(openAIService.getAIRecommendations(any(User.class), any(List.class), any(Integer.class)))
                .thenReturn(List.of(book2));

        // When
        List<RecommendationDTO> result = recommendationService.getRecommendationsForUser(1L, requestDTO);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(RecommendationStrategy.AI_POWERED, result.get(0).getStrategy());
        assertTrue(result.get(0).getReason().contains("AI-powered"));
    }

    @Test
    void getRecommendationsForUser_ShouldThrowException_WhenUserNotFound() {
        // Given
        RecommendationRequestDTO requestDTO = RecommendationRequestDTO.builder().build();
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, 
                    () -> recommendationService.getRecommendationsForUser(999L, requestDTO));
    }

    @Test
    void getRecommendationsForUser_ShouldExcludeAlreadyReviewedBooks() {
        // Given
        RecommendationRequestDTO requestDTO = RecommendationRequestDTO.builder()
                .limit(5)
                .includeTopRated(true)
                .includeGenreBased(false)
                .includeAIPowered(false)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.findByUserId(eq(1L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(review))); // User has reviewed book1
        
        Page<Book> topRatedBooks = new PageImpl<>(List.of(book1, book2)); // Both books returned
        when(bookRepository.findTopRatedBooks(any(BigDecimal.class), any(Integer.class), any(Pageable.class)))
                .thenReturn(topRatedBooks);

        // When
        List<RecommendationDTO> result = recommendationService.getRecommendationsForUser(1L, requestDTO);

        // Then
        assertNotNull(result);
        // Should only recommend book2, not book1 (which user already reviewed)
        assertTrue(result.stream().noneMatch(r -> r.getBook().getId().equals(1L)));
        assertTrue(result.stream().anyMatch(r -> r.getBook().getId().equals(2L)));
    }
}
