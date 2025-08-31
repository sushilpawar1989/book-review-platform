package com.bookreview.book;

import com.bookreview.review.Review;
import com.bookreview.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Book entity representing a book in the platform.
 */
@Entity
@Table(name = "books")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"reviews", "favoritedByUsers"})
@ToString(exclude = {"reviews", "favoritedByUsers"})
@EntityListeners(AuditingEntityListener.class)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Title is mandatory")
    @Size(max = 255, message = "Title should not exceed 255 characters")
    private String title;

    @Column(nullable = false)
    @NotBlank(message = "Author is mandatory")
    @Size(max = 255, message = "Author should not exceed 255 characters")
    private String author;

    @Column(length = 2000)
    @Size(max = 2000, message = "Description should not exceed 2000 characters")
    private String description;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @ElementCollection(targetClass = Genre.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "book_genres", joinColumns = @JoinColumn(name = "book_id"))
    @Column(name = "genre")
    @NotEmpty(message = "At least one genre is required")
    @Builder.Default
    private Set<Genre> genres = new HashSet<>();

    @Column(name = "published_year")
    @Min(value = 1000, message = "Published year must be valid")
    @Max(value = 2100, message = "Published year cannot be in the future")
    private Integer publishedYear;

    @Column(name = "average_rating", precision = 3, scale = 2)
    @DecimalMin(value = "0.0", message = "Average rating cannot be negative")
    @DecimalMax(value = "5.0", message = "Average rating cannot exceed 5.0")
    @Builder.Default
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(name = "total_reviews")
    @Min(value = 0, message = "Total reviews cannot be negative")
    @Builder.Default
    private Integer totalReviews = 0;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Review> reviews = new java.util.ArrayList<>();

    @ManyToMany(mappedBy = "favoriteBooks")
    @Builder.Default
    private Set<User> favoritedByUsers = new java.util.HashSet<>();

    /**
     * Calculate and update average rating from reviews.
     */
    public void updateAverageRating() {
        if (reviews.isEmpty()) {
            this.averageRating = BigDecimal.ZERO;
            this.totalReviews = 0;
            return;
        }

        double average = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);

        this.averageRating = BigDecimal.valueOf(average).setScale(2, java.math.RoundingMode.HALF_UP);
        this.totalReviews = reviews.size();
    }

    /**
     * Check if book has minimum reviews for reliable rating.
     *
     * @param minReviews minimum number of reviews required
     * @return true if book has enough reviews
     */
    public boolean hasMinimumReviews(final int minReviews) {
        return totalReviews >= minReviews;
    }

    /**
     * Get author's last name for sorting purposes.
     *
     * @return author's last name
     */
    public String getAuthorLastName() {
        if (author == null) {
            return "";
        }
        String[] parts = author.split("\\s+");
        return parts.length > 0 ? parts[parts.length - 1] : author;
    }

    /**
     * Check if book has specific genre.
     *
     * @param genre the genre to check
     * @return true if book has the genre
     */
    public boolean hasGenre(final Genre genre) {
        return genres != null && genres.contains(genre);
    }

    /**
     * Add a genre to the book.
     *
     * @param genre the genre to add
     */
    public void addGenre(final Genre genre) {
        if (this.genres == null) {
            this.genres = new HashSet<>();
        }
        this.genres.add(genre);
    }

    /**
     * Remove a genre from the book.
     *
     * @param genre the genre to remove
     */
    public void removeGenre(final Genre genre) {
        if (this.genres != null) {
            this.genres.remove(genre);
        }
    }
}
