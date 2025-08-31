package com.bookreview.dto.book;

import com.bookreview.book.Genre;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

/**
 * DTO for book search criteria.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookSearchDTO {

    private String search; // Search in title and author
    private String title;
    private String author;
    private Set<Genre> genres;
    private Integer publishedYear;
    private Integer minPublishedYear;
    private Integer maxPublishedYear;
    private BigDecimal minRating;
    private BigDecimal maxRating;
    private Integer minReviews;
}
