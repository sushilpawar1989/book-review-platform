package com.bookreview.review;

import com.bookreview.book.Book;
import com.bookreview.book.BookRepository;
import com.bookreview.dto.review.ReviewCreateDTO;
import com.bookreview.dto.review.ReviewDTO;
import com.bookreview.dto.review.ReviewUpdateDTO;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewService reviewService;

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
                .role(UserRole.USER)
                .build();

        book = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .build();

        review = Review.builder()
                .id(1L)
                .book(book)
                .user(user)
                .rating(5)
                .text("Great book!")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createReview_ShouldReturnReviewDTO_WhenValidInput() {
        // Given
        ReviewCreateDTO createDTO = ReviewCreateDTO.builder()
                .bookId(1L)
                .rating(5)
                .text("Great book!")
                .build();

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(reviewRepository.existsByBookIdAndUserId(1L, 1L)).thenReturn(false);
            when(reviewRepository.save(any(Review.class))).thenReturn(review);
            when(bookRepository.save(any(Book.class))).thenReturn(book);

            // When
            ReviewDTO result = reviewService.createReview(createDTO);

            // Then
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals(1L, result.getBookId());
            assertEquals(1L, result.getUserId());
            assertEquals(5, result.getRating());
            assertEquals("Great book!", result.getText());
            verify(reviewRepository).save(any(Review.class));
            verify(bookRepository).save(any(Book.class));
        }
    }

    @Test
    void createReview_ShouldThrowException_WhenUserNotAuthenticated() {
        // Given
        ReviewCreateDTO createDTO = ReviewCreateDTO.builder()
                .bookId(1L)
                .rating(5)
                .text("Great book!")
                .build();

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(null);

            // When & Then
            assertThrows(AccessDeniedException.class, () -> reviewService.createReview(createDTO));
        }
    }

    @Test
    void createReview_ShouldThrowException_WhenUserAlreadyReviewed() {
        // Given
        ReviewCreateDTO createDTO = ReviewCreateDTO.builder()
                .bookId(1L)
                .rating(5)
                .text("Great book!")
                .build();

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(reviewRepository.existsByBookIdAndUserId(1L, 1L)).thenReturn(true);

            // When & Then
            assertThrows(IllegalArgumentException.class, () -> reviewService.createReview(createDTO));
        }
    }

    @Test
    void updateReview_ShouldReturnUpdatedReview_WhenValidInput() {
        // Given
        ReviewUpdateDTO updateDTO = ReviewUpdateDTO.builder()
                .rating(4)
                .text("Updated review")
                .build();

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
            when(reviewRepository.save(any(Review.class))).thenReturn(review);
            when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
            when(bookRepository.save(any(Book.class))).thenReturn(book);

            // When
            ReviewDTO result = reviewService.updateReview(1L, updateDTO);

            // Then
            assertNotNull(result);
            verify(reviewRepository).save(any(Review.class));
            verify(bookRepository).save(any(Book.class)); // Rating changed, so book rating should be updated
        }
    }

    @Test
    void updateReview_ShouldThrowException_WhenUserNotOwner() {
        // Given
        ReviewUpdateDTO updateDTO = ReviewUpdateDTO.builder()
                .rating(4)
                .text("Updated review")
                .build();

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(2L); // Different user

            when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

            // When & Then
            assertThrows(AccessDeniedException.class, () -> reviewService.updateReview(1L, updateDTO));
        }
    }

    @Test
    void deleteReview_ShouldDeleteReview_WhenUserIsOwner() {
        // Given
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            mockedSecurityUtils.when(SecurityUtils::isAdmin).thenReturn(false);

            when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
            when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
            when(bookRepository.save(any(Book.class))).thenReturn(book);

            // When
            reviewService.deleteReview(1L);

            // Then
            verify(reviewRepository).deleteById(1L);
            verify(bookRepository).save(any(Book.class)); // Book rating should be updated
        }
    }

    @Test
    void getReviewsByBookId_ShouldReturnReviews_WhenBookExists() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Review> reviewPage = new PageImpl<>(List.of(review), pageable, 1);

        when(bookRepository.existsById(1L)).thenReturn(true);
        when(reviewRepository.findByBookId(eq(1L), any(Pageable.class))).thenReturn(reviewPage);

        // When
        Page<ReviewDTO> result = reviewService.getReviewsByBookId(1L, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Great book!", result.getContent().get(0).getText());
    }

    @Test
    void getCurrentUserReviewForBook_ShouldReturnReview_WhenUserHasReview() {
        // Given
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            when(reviewRepository.findByBookIdAndUserId(1L, 1L)).thenReturn(Optional.of(review));

            // When
            Optional<ReviewDTO> result = reviewService.getCurrentUserReviewForBook(1L);

            // Then
            assertTrue(result.isPresent());
            assertEquals("Great book!", result.get().getText());
        }
    }

    @Test
    void hasCurrentUserReviewedBook_ShouldReturnTrue_WhenUserHasReviewed() {
        // Given
        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);

            when(reviewRepository.existsByBookIdAndUserId(1L, 1L)).thenReturn(true);

            // When
            boolean result = reviewService.hasCurrentUserReviewedBook(1L);

            // Then
            assertTrue(result);
        }
    }
}
