package com.bookreview.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating an existing review.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewUpdateDTO {

    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private Integer rating;

    @Size(max = 2000, message = "Review text should not exceed 2000 characters")
    private String text;
}
