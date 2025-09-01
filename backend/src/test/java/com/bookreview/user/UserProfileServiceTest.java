package com.bookreview.user;

import com.bookreview.book.Book;
import com.bookreview.book.BookRepository;
import com.bookreview.book.Genre;
import com.bookreview.dto.user.FavoriteBookDTO;
import com.bookreview.dto.user.UserProfileDTO;
import com.bookreview.dto.user.UserUpdateProfileDTO;
import com.bookreview.review.Review;
import com.bookreview.review.ReviewRepository;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private Book book;
    private Review review;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .bio("Test bio")
                .role(UserRole.USER)
                .preferredGenres(Set.of(Genre.FICTION))
                .createdAt(LocalDateTime.now())
                .build();

        book = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .averageRating(BigDecimal.valueOf(4.5))
                .totalReviews(10)
                .genres(Set.of(Genre.FICTION))
                .publishedYear(2023)
                .createdAt(LocalDateTime.now())
                .build();

        review = Review.builder()
                .id(1L)
                .book(book)
                .user(user)
                .rating(5)
                .text("Great book!")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getUserProfile_ShouldReturnUserProfileDTO_WhenUserExists() {
        // Given
        Pageable pageable = PageRequest.of(0, 5);
        Page<Review> reviewPage = new PageImpl<>(List.of(review), pageable, 1);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(reviewRepository.countByUserId(1L)).thenReturn(1L);
        when(reviewRepository.getAverageRatingByUserId(1L)).thenReturn(4.5);
        when(reviewRepository.findByUserId(eq(1L), any(Pageable.class))).thenReturn(reviewPage);

        // When
        UserProfileDTO result = userService.getUserProfile(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("Test bio", result.getBio());
        assertEquals(1, result.getTotalReviews());
        assertEquals(4.5, result.getAverageRating());
        assertEquals(1, result.getRecentReviews().size());
    }

    @Test
    void getCurrentUserProfile_ShouldReturnProfile_WhenUserAuthenticated() {
        // Given
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            Pageable pageable = PageRequest.of(0, 5);
            Page<Review> reviewPage = new PageImpl<>(List.of(review), pageable, 1);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(reviewRepository.countByUserId(1L)).thenReturn(1L);
            when(reviewRepository.getAverageRatingByUserId(1L)).thenReturn(4.5);
            when(reviewRepository.findByUserId(eq(1L), any(Pageable.class))).thenReturn(reviewPage);

            // When
            UserProfileDTO result = userService.getCurrentUserProfile();

            // Then
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("John", result.getFirstName());
        }
    }

    @Test
    void getCurrentUserProfile_ShouldThrowException_WhenUserNotAuthenticated() {
        // Given
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(null);

            // When & Then
            assertThrows(AccessDeniedException.class, () -> userService.getCurrentUserProfile());
        }
    }

    @Test
    void updateCurrentUserProfile_ShouldUpdateProfile_WhenValidInput() {
        // Given
        UserUpdateProfileDTO updateDTO = UserUpdateProfileDTO.builder()
                .firstName("Jane")
                .bio("Updated bio")
                .preferredGenres(Set.of(Genre.MYSTERY))
                .build();

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            Pageable pageable = PageRequest.of(0, 5);
            Page<Review> reviewPage = new PageImpl<>(List.of(review), pageable, 1);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(reviewRepository.countByUserId(1L)).thenReturn(1L);
            when(reviewRepository.getAverageRatingByUserId(1L)).thenReturn(4.5);
            when(reviewRepository.findByUserId(eq(1L), any(Pageable.class))).thenReturn(reviewPage);

            // When
            UserProfileDTO result = userService.updateCurrentUserProfile(updateDTO);

            // Then
            assertNotNull(result);
            verify(userRepository).save(any(User.class));
        }
    }

    @Test
    void addBookToFavorites_ShouldAddBook_WhenValidInput() {
        // Given
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            when(userRepository.existsById(1L)).thenReturn(true);
            when(bookRepository.existsById(1L)).thenReturn(true);
            when(userRepository.existsFavoriteBookByUserIdAndBookId(1L, 1L)).thenReturn(false);

            // When
            userService.addBookToFavorites(1L);

            // Then
            verify(userRepository).addBookToUserFavorites(1L, 1L);
        }
    }

    @Test
    void addBookToFavorites_ShouldDoNothing_WhenBookAlreadyInFavorites() {
        // Given
        user.getFavoriteBooks().add(book); // Book already in favorites

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            when(userRepository.existsById(1L)).thenReturn(true);
            when(bookRepository.existsById(1L)).thenReturn(true);
            when(userRepository.existsFavoriteBookByUserIdAndBookId(1L, 1L)).thenReturn(true);

            // When
            userService.addBookToFavorites(1L);

            // Then - should not call addBookToUserFavorites since book is already in favorites
            verify(userRepository, never()).addBookToUserFavorites(1L, 1L);
        }
    }

    @Test
    void removeBookFromFavorites_ShouldRemoveBook_WhenBookInFavorites() {
        // Given
        user.getFavoriteBooks().add(book); // Book in favorites

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            when(userRepository.existsById(1L)).thenReturn(true);
            when(bookRepository.existsById(1L)).thenReturn(true);
            when(userRepository.existsFavoriteBookByUserIdAndBookId(1L, 1L)).thenReturn(true);

            // When
            userService.removeBookFromFavorites(1L);

            // Then
            verify(userRepository).removeBookFromUserFavorites(1L, 1L);
        }
    }

    @Test
    void getCurrentUserFavoriteBooks_ShouldReturnFavorites_WhenUserAuthenticated() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> bookPage = new PageImpl<>(List.of(book), pageable, 1);

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            when(userRepository.findFavoriteBooksByUserId(eq(1L), any(Pageable.class))).thenReturn(bookPage);
            when(reviewRepository.existsByBookIdAndUserId(1L, 1L)).thenReturn(true);

            // When
            Page<FavoriteBookDTO> result = userService.getCurrentUserFavoriteBooks(pageable);

            // Then
            assertNotNull(result);
            assertEquals(1, result.getTotalElements());
            assertEquals("Test Book", result.getContent().get(0).getTitle());
            assertTrue(result.getContent().get(0).getHasUserReviewed());
        }
    }

    @Test
    void isBookInCurrentUserFavorites_ShouldReturnTrue_WhenBookInFavorites() {
        // Given
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            when(userRepository.existsFavoriteBookByUserIdAndBookId(1L, 1L)).thenReturn(true);

            // When
            boolean result = userService.isBookInCurrentUserFavorites(1L);

            // Then
            assertTrue(result);
        }
    }

    @Test
    void isBookInCurrentUserFavorites_ShouldReturnFalse_WhenUserNotAuthenticated() {
        // Given
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(null);

            // When
            boolean result = userService.isBookInCurrentUserFavorites(1L);

            // Then
            assertFalse(result);
        }
    }
}
