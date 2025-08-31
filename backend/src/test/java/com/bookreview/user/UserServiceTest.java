package com.bookreview.user;

import com.bookreview.book.Book;
import com.bookreview.book.BookRepository;
import com.bookreview.book.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for UserService.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private UserService userService;

    private User user;
    private Book book;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("John")
                .lastName("Doe")
                .bio("Test user")
                .role(UserRole.USER)
                .preferredGenres(Set.of(Genre.FICTION))
                .build();

        book = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .genres(Set.of(Genre.FICTION))
                .publishedYear(2023)
                .build();
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = userService.loadUserByUsername("test@example.com");

        // Then
        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class, 
                () -> userService.loadUserByUsername("nonexistent@example.com"));
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.getUserById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
    }

    @Test
    void getUserByEmail_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.getUserByEmail("test@example.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void updateUserProfile_ShouldUpdateAndReturnUser_WhenUserExists() {
        // Given
        User updatedUser = User.builder()
                .firstName("Jane")
                .lastName("Smith")
                .bio("Updated bio")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        User result = userService.updateUserProfile(1L, updatedUser);

        // Then
        assertNotNull(result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUserProfile_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, 
                () -> userService.updateUserProfile(1L, user));
    }

    @Test
    void addToFavorites_ShouldAddBookToFavorites_WhenUserAndBookExist() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        userService.addToFavorites(1L, 1L);

        // Then
        verify(userRepository).save(any(User.class));
    }

    @Test
    void addToFavorites_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, 
                () -> userService.addToFavorites(1L, 1L));
    }

    @Test
    void addToFavorites_ShouldThrowException_WhenBookNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, 
                () -> userService.addToFavorites(1L, 1L));
    }

    @Test
    void removeFromFavorites_ShouldRemoveBookFromFavorites_WhenUserAndBookExist() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        userService.removeFromFavorites(1L, 1L);

        // Then
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getFavoriteBooks_ShouldReturnFavoriteBooks_WhenUserExists() {
        // Given
        user.getFavoriteBooks().add(book);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        Set<Book> result = userService.getFavoriteBooks(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.contains(book));
    }

    @Test
    void getFavoriteBooks_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, 
                () -> userService.getFavoriteBooks(1L));
    }
}
