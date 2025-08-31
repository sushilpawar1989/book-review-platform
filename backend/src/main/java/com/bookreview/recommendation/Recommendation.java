package com.bookreview.recommendation;

import com.bookreview.book.Book;
import com.bookreview.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Recommendation entity representing a book recommendation for a user.
 */
@Entity
@Table(name = "recommendations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is mandatory")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    @NotNull(message = "Book is mandatory")
    private Book book;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Strategy is mandatory")
    private RecommendationStrategy strategy;

    @Column(precision = 5, scale = 4)
    @DecimalMin(value = "0.0", message = "Confidence cannot be negative")
    @DecimalMax(value = "1.0", message = "Confidence cannot exceed 1.0")
    @Builder.Default
    private BigDecimal confidence = BigDecimal.ZERO;

    @Column(length = 1000)
    private String reasoning;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    /**
     * Check if recommendation is expired.
     *
     * @return true if recommendation is expired
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * Check if recommendation is valid (not expired).
     *
     * @return true if recommendation is still valid
     */
    public boolean isValid() {
        return !isExpired();
    }

    /**
     * Get confidence score as percentage.
     *
     * @return confidence as percentage (0-100)
     */
    public int getConfidencePercentage() {
        return confidence.multiply(BigDecimal.valueOf(100)).intValue();
    }

    /**
     * Check if confidence is high (above threshold).
     *
     * @param threshold confidence threshold (0.0 to 1.0)
     * @return true if confidence is above threshold
     */
    public boolean hasHighConfidence(final double threshold) {
        return confidence.compareTo(BigDecimal.valueOf(threshold)) > 0;
    }

    /**
     * Set expiration time from now.
     *
     * @param hours hours from now to expire
     */
    public void setExpiresInHours(final int hours) {
        this.expiresAt = LocalDateTime.now().plusHours(hours);
    }

    /**
     * Get age of recommendation in hours.
     *
     * @return age in hours
     */
    public long getAgeInHours() {
        if (createdAt == null) {
            return 0;
        }
        return java.time.Duration.between(createdAt, LocalDateTime.now()).toHours();
    }
}
