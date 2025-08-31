package com.bookreview.dto.book;

import com.bookreview.book.Genre;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO for updating an existing book.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookUpdateDTO {

    @Size(max = 255, message = "Title should not exceed 255 characters")
    private String title;

    @Size(max = 255, message = "Author should not exceed 255 characters")
    private String author;

    @Size(max = 2000, message = "Description should not exceed 2000 characters")
    private String description;

    private String coverImageUrl;

    private Set<Genre> genres;

    @Min(value = 1000, message = "Published year must be valid")
    @Max(value = 2100, message = "Published year cannot be in the future")
    private Integer publishedYear;
}
