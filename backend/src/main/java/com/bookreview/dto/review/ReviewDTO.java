package com.bookreview.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for review information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {

    private Long id;
    private Long bookId;
    private String bookTitle;
    private Long userId;
    private String userFirstName;
    private String userLastName;
    private Integer rating;
    private String text;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
