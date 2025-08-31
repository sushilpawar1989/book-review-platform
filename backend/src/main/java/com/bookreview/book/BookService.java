package com.bookreview.book;

import com.bookreview.dto.book.BookCreateDTO;
import com.bookreview.dto.book.BookDTO;
import com.bookreview.dto.book.BookSearchDTO;
import com.bookreview.dto.book.BookUpdateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Service class for book operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookService {

    private final BookRepository bookRepository;

    /**
     * Get all books with pagination.
     *
     * @param pageable pagination information
     * @return page of book DTOs
     */
    @Transactional(readOnly = true)
    public Page<BookDTO> getAllBooks(final Pageable pageable) {
        log.debug("Getting all books with pagination: {}", pageable);
        return bookRepository.findAll(pageable).map(this::mapToDTO);
    }

    /**
     * Get book by ID.
     *
     * @param id book ID
     * @return optional book DTO
     */
    @Transactional(readOnly = true)
    public Optional<BookDTO> getBookById(final Long id) {
        log.debug("Getting book by ID: {}", id);
        return bookRepository.findById(id).map(this::mapToDTO);
    }

    /**
     * Create a new book.
     *
     * @param createDTO book creation data
     * @return created book DTO
     */
    public BookDTO createBook(final BookCreateDTO createDTO) {
        log.info("Creating new book: {}", createDTO.getTitle());
        
        // Check if book with same title already exists
        if (bookRepository.existsByTitle(createDTO.getTitle())) {
            throw new IllegalArgumentException("Book with title '" + createDTO.getTitle() + "' already exists");
        }
        
        Book book = Book.builder()
                .title(createDTO.getTitle())
                .author(createDTO.getAuthor())
                .description(createDTO.getDescription())
                .coverImageUrl(createDTO.getCoverImageUrl())
                .genres(createDTO.getGenres())
                .publishedYear(createDTO.getPublishedYear())
                .build();
        
        Book savedBook = bookRepository.save(book);
        log.info("Book created successfully with ID: {}", savedBook.getId());
        
        return mapToDTO(savedBook);
    }

    /**
     * Update an existing book.
     *
     * @param id book ID
     * @param updateDTO book update data
     * @return updated book DTO
     */
    public BookDTO updateBook(final Long id, final BookUpdateDTO updateDTO) {
        log.info("Updating book with ID: {}", id);
        
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));
                
        // Update fields if provided
        if (updateDTO.getTitle() != null) {
            existingBook.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getAuthor() != null) {
            existingBook.setAuthor(updateDTO.getAuthor());
        }
        if (updateDTO.getDescription() != null) {
            existingBook.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getCoverImageUrl() != null) {
            existingBook.setCoverImageUrl(updateDTO.getCoverImageUrl());
        }
        if (updateDTO.getGenres() != null && !updateDTO.getGenres().isEmpty()) {
            existingBook.setGenres(updateDTO.getGenres());
        }
        if (updateDTO.getPublishedYear() != null) {
            existingBook.setPublishedYear(updateDTO.getPublishedYear());
        }
        
        Book savedBook = bookRepository.save(existingBook);
        log.info("Book updated successfully");
        
        return mapToDTO(savedBook);
    }

    /**
     * Delete a book.
     *
     * @param id book ID
     */
    public void deleteBook(final Long id) {
        log.info("Deleting book with ID: {}", id);
        
        if (!bookRepository.existsById(id)) {
            throw new IllegalArgumentException("Book not found: " + id);
        }
        
        bookRepository.deleteById(id);
    }

    /**
     * Search books by title, author, or description.
     *
     * @param searchTerm search term
     * @param pageable pagination information
     * @return page of book DTOs
     */
    @Transactional(readOnly = true)
    public Page<BookDTO> searchBooks(final String searchTerm, final Pageable pageable) {
        log.debug("Searching books with term: {}", searchTerm);
        return bookRepository.searchBooks(searchTerm, pageable).map(this::mapToDTO);
    }

    /**
     * Search books with advanced criteria.
     *
     * @param searchDTO search criteria
     * @param pageable pagination information
     * @return page of book DTOs
     */
    @Transactional(readOnly = true)
    public Page<BookDTO> searchBooksAdvanced(final BookSearchDTO searchDTO, final Pageable pageable) {
        log.debug("Advanced search with criteria: {}", searchDTO);
        
        if (searchDTO.getSearch() != null && !searchDTO.getSearch().trim().isEmpty()) {
            return bookRepository.searchBooks(searchDTO.getSearch(), pageable).map(this::mapToDTO);
        }
        
        // For advanced search, fall back to basic search for now
        // Complex filtering can be implemented later with a proper query
        String searchTerm = "";
        if (searchDTO.getTitle() != null && !searchDTO.getTitle().trim().isEmpty()) {
            searchTerm = searchDTO.getTitle();
        } else if (searchDTO.getAuthor() != null && !searchDTO.getAuthor().trim().isEmpty()) {
            searchTerm = searchDTO.getAuthor();
        }
        
        if (!searchTerm.isEmpty()) {
            return bookRepository.searchBooks(searchTerm, pageable).map(this::mapToDTO);
        } else {
            return bookRepository.findAll(pageable).map(this::mapToDTO);
        }
    }

    /**
     * Search books by title and/or author.
     *
     * @param title title search term
     * @param author author search term
     * @param pageable pagination information
     * @return page of book DTOs
     */
    @Transactional(readOnly = true)
    public Page<BookDTO> searchByTitleAndAuthor(final String title, final String author, final Pageable pageable) {
        log.debug("Searching books by title: {} and author: {}", title, author);
        return bookRepository.findByTitleAndAuthor(title, author, pageable).map(this::mapToDTO);
    }

    /**
     * Find books by genre.
     *
     * @param genre book genre
     * @param pageable pagination information
     * @return page of book DTOs
     */
    @Transactional(readOnly = true)
    public Page<BookDTO> getBooksByGenre(final Genre genre, final Pageable pageable) {
        log.debug("Getting books by genre: {}", genre);
        return bookRepository.findByGenre(genre, pageable).map(this::mapToDTO);
    }

    /**
     * Find books by published year.
     *
     * @param publishedYear published year
     * @param pageable pagination information
     * @return page of book DTOs
     */
    @Transactional(readOnly = true)
    public Page<BookDTO> getBooksByPublishedYear(final Integer publishedYear, final Pageable pageable) {
        log.debug("Getting books by published year: {}", publishedYear);
        return bookRepository.findByPublishedYear(publishedYear, pageable).map(this::mapToDTO);
    }

    /**
     * Find top-rated books.
     *
     * @param minRating minimum rating
     * @param minReviews minimum number of reviews
     * @param pageable pagination information
     * @return page of top-rated book DTOs
     */
    @Transactional(readOnly = true)
    public Page<BookDTO> getTopRatedBooks(final BigDecimal minRating, 
                                         final int minReviews, 
                                         final Pageable pageable) {
        log.debug("Getting top-rated books with min rating: {} and min reviews: {}", minRating, minReviews);
        return bookRepository.findTopRatedBooks(minRating, minReviews, pageable).map(this::mapToDTO);
    }

    /**
     * Update book's average rating.
     *
     * @param bookId book ID
     */
    public void updateBookRating(final Long bookId) {
        log.debug("Updating rating for book ID: {}", bookId);
        
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));
                
        book.updateAverageRating();
        bookRepository.save(book);
    }

    /**
     * Map Book entity to BookDTO.
     *
     * @param book the book entity
     * @return book DTO
     */
    private BookDTO mapToDTO(final Book book) {
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
