package com.bookreview.book;

import com.bookreview.dto.book.BookCreateDTO;
import com.bookreview.dto.book.BookDTO;
import com.bookreview.dto.book.BookSearchDTO;
import com.bookreview.dto.book.BookUpdateDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Set;

/**
 * REST controller for book endpoints.
 */
@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {

    private final BookService bookService;

    /**
     * Get all books with pagination and filtering.
     *
     * @param pageable pagination parameters
     * @param genres filter by genres
     * @param author filter by author
     * @param title filter by title
     * @param search general search term
     * @param publishedYear filter by published year
     * @param minPublishedYear minimum published year
     * @param maxPublishedYear maximum published year
     * @param minRating minimum rating filter
     * @param maxRating maximum rating filter
     * @param minReviews minimum number of reviews
     * @return page of books
     */
    @GetMapping
    public ResponseEntity<Page<BookDTO>> getAllBooks(
            @PageableDefault(size = 20) final Pageable pageable,
            @RequestParam(required = false) final Set<Genre> genres,
            @RequestParam(required = false) final String author,
            @RequestParam(required = false) final String title,
            @RequestParam(required = false) final String search,
            @RequestParam(required = false) final Integer publishedYear,
            @RequestParam(required = false) final Integer minPublishedYear,
            @RequestParam(required = false) final Integer maxPublishedYear,
            @RequestParam(required = false) final BigDecimal minRating,
            @RequestParam(required = false) final BigDecimal maxRating,
            @RequestParam(required = false) final Integer minReviews) {
        
        log.debug("Getting books with filters - genres: {}, author: {}, title: {}, search: {}", 
                  genres, author, title, search);

        // If simple search term is provided, use general search
        if (search != null && !search.trim().isEmpty()) {
            Page<BookDTO> books = bookService.searchBooks(search, pageable);
            return ResponseEntity.ok(books);
        }

        // Build advanced search criteria
        BookSearchDTO searchDTO = BookSearchDTO.builder()
                .title(title)
                .author(author)
                .genres(genres)
                .publishedYear(publishedYear)
                .minPublishedYear(minPublishedYear)
                .maxPublishedYear(maxPublishedYear)
                .minRating(minRating)
                .maxRating(maxRating)
                .minReviews(minReviews)
                .build();

        Page<BookDTO> books = bookService.searchBooksAdvanced(searchDTO, pageable);
        return ResponseEntity.ok(books);
    }

    /**
     * Get book by ID.
     *
     * @param id book ID
     * @return book details
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable final Long id) {
        log.debug("Getting book by ID: {}", id);

        return bookService.getBookById(id)
                .map(book -> ResponseEntity.ok(book))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create a new book (Admin only).
     *
     * @param createDTO book creation data
     * @return created book
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookDTO> createBook(@Valid @RequestBody final BookCreateDTO createDTO) {
        log.info("Creating new book: {}", createDTO.getTitle());

        try {
            BookDTO createdBook = bookService.createBook(createDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
        } catch (IllegalArgumentException e) {
            log.error("Error creating book: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update an existing book (Admin only).
     *
     * @param id book ID
     * @param updateDTO book update data
     * @return updated book
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookDTO> updateBook(@PathVariable final Long id, 
                                             @Valid @RequestBody final BookUpdateDTO updateDTO) {
        log.info("Updating book with ID: {}", id);

        try {
            BookDTO updatedBook = bookService.updateBook(id, updateDTO);
            return ResponseEntity.ok(updatedBook);
        } catch (IllegalArgumentException e) {
            log.error("Error updating book: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a book (Admin only).
     *
     * @param id book ID
     * @return success response
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable final Long id) {
        log.info("Deleting book with ID: {}", id);

        try {
            bookService.deleteBook(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get top-rated books.
     *
     * @param pageable pagination parameters
     * @param minRating minimum rating (default: 4.0)
     * @param minReviews minimum number of reviews (default: 5)
     * @return page of top-rated books
     */
    @GetMapping("/top-rated")
    public ResponseEntity<Page<BookDTO>> getTopRatedBooks(
            @PageableDefault(size = 20) final Pageable pageable,
            @RequestParam(defaultValue = "4.0") final BigDecimal minRating,
            @RequestParam(defaultValue = "5") final int minReviews) {
        
        log.debug("Getting top-rated books with min rating: {} and min reviews: {}", minRating, minReviews);

        Page<BookDTO> topRatedBooks = bookService.getTopRatedBooks(minRating, minReviews, pageable);
        return ResponseEntity.ok(topRatedBooks);
    }

    /**
     * Search books by title and/or author.
     *
     * @param title title search term
     * @param author author search term
     * @param pageable pagination parameters
     * @return page of matching books
     */
    @GetMapping("/search")
    public ResponseEntity<Page<BookDTO>> searchBooks(
            @RequestParam(required = false) final String title,
            @RequestParam(required = false) final String author,
            @PageableDefault(size = 20) final Pageable pageable) {
        
        log.debug("Searching books with title: {} and author: {}", title, author);

        if ((title == null || title.trim().isEmpty()) && (author == null || author.trim().isEmpty())) {
            return ResponseEntity.badRequest().build();
        }

        Page<BookDTO> books = bookService.searchByTitleAndAuthor(title, author, pageable);
        return ResponseEntity.ok(books);
    }

    /**
     * Get books by genre.
     *
     * @param genre book genre
     * @param pageable pagination parameters
     * @return page of books in the genre
     */
    @GetMapping("/genre/{genre}")
    public ResponseEntity<Page<BookDTO>> getBooksByGenre(
            @PathVariable final Genre genre,
            @PageableDefault(size = 20) final Pageable pageable) {
        
        log.debug("Getting books by genre: {}", genre);

        Page<BookDTO> books = bookService.getBooksByGenre(genre, pageable);
        return ResponseEntity.ok(books);
    }

    /**
     * Get books by published year.
     *
     * @param year published year
     * @param pageable pagination parameters
     * @return page of books published in the year
     */
    @GetMapping("/year/{year}")
    public ResponseEntity<Page<BookDTO>> getBooksByYear(
            @PathVariable final Integer year,
            @PageableDefault(size = 20) final Pageable pageable) {
        
        log.debug("Getting books by published year: {}", year);

        Page<BookDTO> books = bookService.getBooksByPublishedYear(year, pageable);
        return ResponseEntity.ok(books);
    }
}
