package com.bookreview.service;

import com.bookreview.book.Book;
import com.bookreview.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for AI-powered book recommendations using OpenAI.
 * This is a stub implementation that will be replaced with actual OpenAI integration.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIRecommendationService {

    @Value("${application.openai.api-key:}")
    private String openAIApiKey;

    @Value("${application.openai.enabled:false}")
    private boolean openAIEnabled;

    /**
     * Get AI-powered book recommendations for a user.
     *
     * @param user the user to get recommendations for
     * @param userBooks books the user has already read/reviewed
     * @param limit maximum number of recommendations
     * @return list of recommended books
     */
    public List<Book> getAIRecommendations(final User user, final List<Book> userBooks, final int limit) {
        log.info("Getting AI recommendations for user: {} (limit: {})", user.getId(), limit);
        
        if (!openAIEnabled || openAIApiKey == null || openAIApiKey.isEmpty()) {
            log.warn("OpenAI integration is not enabled or API key is missing");
            return List.of(); // Return empty list when not configured
        }
        
        // TODO: Implement actual OpenAI API integration
        // This is where you would:
        // 1. Build a prompt based on user preferences, favorite genres, and read books
        // 2. Call OpenAI API to get book recommendations
        // 3. Parse the response and match recommendations to books in the database
        // 4. Return the recommended books
        
        return mockAIRecommendations(user, userBooks, limit);
    }

    /**
     * Check if OpenAI integration is available.
     *
     * @return true if OpenAI can be used for recommendations
     */
    public boolean isAvailable() {
        return openAIEnabled && openAIApiKey != null && !openAIApiKey.isEmpty();
    }

    /**
     * Mock implementation for AI recommendations.
     * This simulates what an AI service might return.
     *
     * @param user the user
     * @param userBooks books already read by user
     * @param limit recommendation limit
     * @return mock recommended books
     */
    private List<Book> mockAIRecommendations(final User user, final List<Book> userBooks, final int limit) {
        log.debug("Returning mock AI recommendations for user: {}", user.getId());
        
        // In a real implementation, this would be replaced with actual OpenAI API calls
        // For now, return empty list to avoid confusion
        return List.of();
    }

    /**
     * Generate a prompt for OpenAI based on user preferences.
     *
     * @param user the user
     * @param userBooks books the user has read
     * @return OpenAI prompt string
     */
    private String buildRecommendationPrompt(final User user, final List<Book> userBooks) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Based on the following user profile, recommend books:\n\n");
        prompt.append("User preferences:\n");
        prompt.append("- Preferred genres: ").append(user.getPreferredGenres()).append("\n");
        prompt.append("- Bio: ").append(user.getBio() != null ? user.getBio() : "No bio provided").append("\n\n");
        
        if (!userBooks.isEmpty()) {
            prompt.append("Books the user has read and reviewed:\n");
            userBooks.forEach(book -> {
                prompt.append("- ").append(book.getTitle())
                      .append(" by ").append(book.getAuthor())
                      .append(" (Genres: ").append(book.getGenres()).append(")\n");
            });
        }
        
        prompt.append("\nPlease recommend ").append("books that would appeal to this user. ");
        prompt.append("Provide the title, author, and a brief reason for each recommendation.");
        
        return prompt.toString();
    }
}
