package com.bookreview.recommendation;

/**
 * Enumeration representing different recommendation strategies.
 */
public enum RecommendationStrategy {
    /**
     * Recommend top-rated books.
     */
    TOP_RATED,
    
    /**
     * Recommend books based on genre similarity.
     */
    GENRE_SIMILARITY,
    
    /**
     * Recommend books based on user's favorites similarity.
     */
    FAVORITES_SIMILARITY,
    
    /**
     * AI-powered recommendations using external API.
     */
    AI_POWERED
}
