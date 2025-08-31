import { apiClient } from './client'
import { 
  RecommendationRequest, 
  RecommendationResponse, 
  RecommendationFilters,
  RecommendationStrategy 
} from '@/types/recommendation'

export const recommendationsApi = {
  // Get personalized recommendations for the current user
  getRecommendations: async (params?: RecommendationRequest): Promise<RecommendationResponse> => {
    const response = await apiClient.get('/recommendations/for-me', { params })
    return { recommendations: response.data } // Wrap in expected format
  },

  // Get recommendations by specific strategy
  getRecommendationsByStrategy: async (
    strategy: RecommendationStrategy, 
    params?: RecommendationRequest
  ): Promise<RecommendationResponse> => {
    // Map strategies to available endpoints
    switch (strategy) {
      case RecommendationStrategy.TOP_RATED:
        const topRatedResponse = await apiClient.get('/recommendations/top-rated', { params })
        return { recommendations: topRatedResponse.data }
      case RecommendationStrategy.GENRE_BASED:
        const genreResponse = await apiClient.get('/recommendations/for-me', { 
          params: { 
            includeTopRated: false,
            includeGenreBased: true,
            includeAIPowered: false,
            limit: 10
          } 
        })
        return { recommendations: genreResponse.data }
      default:
        const defaultResponse = await apiClient.get('/recommendations/for-me', { params })
        return { recommendations: defaultResponse.data }
    }
  },

  // Get top-rated books recommendations
  getTopRatedRecommendations: async (params?: RecommendationRequest): Promise<RecommendationResponse> => {
    const response = await apiClient.get('/recommendations/top-rated', { params })
    return { recommendations: response.data } // Wrap in expected format
  },

  // Get genre-based recommendations
  getRecommendationsByGenre: async (genres: string[]): Promise<RecommendationResponse> => {
    // Use the /for-me endpoint with genre-based filtering enabled
    const response = await apiClient.get('/recommendations/for-me', { 
      params: { 
        includeTopRated: false,
        includeGenreBased: true,
        includeAIPowered: false,
        limit: 10
      } 
    })
    return { recommendations: response.data }
  },

  // Get trending books recommendations (using top-rated as fallback)
  getTrendingRecommendations: async (params?: RecommendationRequest): Promise<RecommendationResponse> => {
    // Use top-rated endpoint as trending fallback
    const response = await apiClient.get('/recommendations/top-rated', { params })
    return { recommendations: response.data }
  },

  // Refresh recommendations (just refetch for now)
  refreshRecommendations: async (): Promise<RecommendationResponse> => {
    // For now, just return empty - the hooks will refetch automatically
    return { recommendations: [] }
  },

  // Provide feedback on a recommendation (placeholder)
  provideFeedback: async (bookId: string, helpful: boolean, reason?: string): Promise<void> => {
    // Placeholder - no backend endpoint yet
    console.log('Feedback provided:', { bookId, helpful, reason })
  },
}
