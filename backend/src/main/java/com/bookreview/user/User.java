package com.bookreview.user;

import com.bookreview.book.Book;
import com.bookreview.book.Genre;
import com.bookreview.review.Review;
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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User entity representing a platform user.
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"reviews", "favoriteBooks"})
@ToString(exclude = {"reviews", "favoriteBooks"})
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @Column(nullable = false)
    @NotBlank(message = "Password is mandatory")
    private String password;

    @Column(name = "first_name", nullable = false)
    @NotBlank(message = "First name is mandatory")
    @Size(max = 50, message = "First name should not exceed 50 characters")
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @NotBlank(message = "Last name is mandatory")
    @Size(max = 50, message = "Last name should not exceed 50 characters")
    private String lastName;

    @Column(length = 500)
    @Size(max = 500, message = "Bio should not exceed 500 characters")
    private String bio;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserRole role = UserRole.USER;

    @ElementCollection(targetClass = Genre.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_preferred_genres", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "preferred_genres")
    @Builder.Default
    private Set<Genre> preferredGenres = new HashSet<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Review> reviews = new java.util.ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_favorite_books",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    @Builder.Default
    private Set<Book> favoriteBooks = new HashSet<>();

    /**
     * Get user's full name.
     *
     * @return concatenated first and last name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Check if user is admin.
     *
     * @return true if user has admin role
     */
    public boolean isAdmin() {
        return UserRole.ADMIN.equals(role);
    }

    /**
     * Add a book to favorites.
     *
     * @param book the book to add
     */
    public void addFavoriteBook(final Book book) {
        favoriteBooks.add(book);
    }

    /**
     * Remove a book from favorites.
     *
     * @param book the book to remove
     */
    public void removeFavoriteBook(final Book book) {
        favoriteBooks.remove(book);
    }

    /**
     * Check if a book is in favorites.
     *
     * @param book the book to check
     * @return true if book is in favorites
     */
    public boolean isFavoriteBook(final Book book) {
        return favoriteBooks.contains(book);
    }

}
