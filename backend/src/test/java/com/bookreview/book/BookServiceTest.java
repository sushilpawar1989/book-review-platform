package com.bookreview.book;

import com.bookreview.dto.book.BookDTO;
import com.bookreview.dto.book.BookCreateDTO;
import com.bookreview.dto.book.BookUpdateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for BookService.
 */
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book book;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        book = Book.builder()
                .id(1L)
                .title("Test Book")
                .author("Test Author")
                .description("Test description")
                .genres(Set.of(Genre.FICTION))
                .publishedYear(2023)
                .averageRating(BigDecimal.valueOf(4.5))
                .totalReviews(10)
                .build();

        pageable = PageRequest.of(0, 20);
    }

    @Test
    void getAllBooks_ShouldReturnPageOfBooks() {
        // Given
        Page<Book> expectedPage = new PageImpl<>(List.of(book));
        when(bookRepository.findAll(pageable)).thenReturn(expectedPage);

        // When
        Page<BookDTO> result = bookService.getAllBooks(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Test Book", result.getContent().get(0).getTitle());
    }

    @Test
    void getBookById_ShouldReturnBook_WhenBookExists() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        // When
        Optional<BookDTO> result = bookService.getBookById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Test Book", result.get().getTitle());
    }

    @Test
    void createBook_ShouldSaveAndReturnBook_WhenISBNIsUnique() {
        // Given
        when(bookRepository.existsByTitle(book.getTitle())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // When
        BookCreateDTO createDTO = BookCreateDTO.builder()
                .title(book.getTitle())
                .author(book.getAuthor())
                .description(book.getDescription())
                .genres(book.getGenres())
                .publishedYear(book.getPublishedYear())
                .build();
        BookDTO result = bookService.createBook(createDTO);

        // Then
        assertNotNull(result);
        assertEquals("Test Book", result.getTitle());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void createBook_ShouldThrowException_WhenISBNAlreadyExists() {
        // Given
        when(bookRepository.existsByTitle(book.getTitle())).thenReturn(true);
        // Don't mock save() when we expect an exception to be thrown before save() is called

        // When & Then
        BookCreateDTO createDTO = BookCreateDTO.builder()
                .title(book.getTitle())
                .author(book.getAuthor())
                .description(book.getDescription())
                .genres(book.getGenres())
                .publishedYear(book.getPublishedYear())
                .build();
        assertThrows(IllegalArgumentException.class, () -> bookService.createBook(createDTO));
    }

    @Test
    void updateBook_ShouldUpdateAndReturnBook_WhenBookExists() {
        // Given
        BookUpdateDTO updateDTO = BookUpdateDTO.builder()
                .title("Updated Title")
                .author("Updated Author")
                .genres(Set.of(Genre.NON_FICTION))
                .description("Updated description")
                .build();
        
        Book updatedBook = Book.builder()
                .id(1L)
                .title("Updated Title")
                .author("Updated Author")
                .genres(Set.of(Genre.NON_FICTION))
                .description("Updated description")
                .publishedYear(2023)
                .build();

        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);

        // When
        BookDTO result = bookService.updateBook(1L, updateDTO);

        // Then
        assertNotNull(result);
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void updateBook_ShouldThrowException_WhenBookNotFound() {
        // Given
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        BookUpdateDTO updateDTO = BookUpdateDTO.builder()
                .title(book.getTitle())
                .author(book.getAuthor())
                .description(book.getDescription())
                .genres(book.getGenres())
                .publishedYear(book.getPublishedYear())
                .build();
        assertThrows(IllegalArgumentException.class, () -> bookService.updateBook(1L, updateDTO));
    }

    @Test
    void deleteBook_ShouldDeleteBook_WhenBookExists() {
        // Given
        when(bookRepository.existsById(1L)).thenReturn(true);

        // When
        bookService.deleteBook(1L);

        // Then
        verify(bookRepository).deleteById(1L);
    }

    @Test
    void deleteBook_ShouldThrowException_WhenBookNotFound() {
        // Given
        when(bookRepository.existsById(anyLong())).thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> bookService.deleteBook(1L));
    }

    @Test
    void searchBooks_ShouldReturnPageOfBooks() {
        // Given
        Page<Book> expectedPage = new PageImpl<>(List.of(book));
        when(bookRepository.searchBooks(anyString(), any(Pageable.class))).thenReturn(expectedPage);

        // When
        Page<BookDTO> result = bookService.searchBooks("test", pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getBooksByGenre_ShouldReturnPageOfBooks() {
        // Given
        Page<Book> expectedPage = new PageImpl<>(List.of(book));
        when(bookRepository.findByGenre(Genre.FICTION, pageable)).thenReturn(expectedPage);

        // When
        Page<BookDTO> result = bookService.getBooksByGenre(Genre.FICTION, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertTrue(result.getContent().get(0).getGenres().contains(Genre.FICTION));
    }

    @Test
    void updateBookRating_ShouldUpdateRating_WhenBookExists() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        // When
        bookService.updateBookRating(1L);

        // Then
        verify(bookRepository).save(any(Book.class));
    }
}
