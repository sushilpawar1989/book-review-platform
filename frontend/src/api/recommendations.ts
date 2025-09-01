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
    return { 
      recommendations: response.data,
      strategy: RecommendationStrategy.PERSONALIZED,
      totalCount: response.data?.length || 0,
      generated: new Date().toISOString()
    }
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
        return { 
          recommendations: topRatedResponse.data,
          strategy: RecommendationStrategy.TOP_RATED,
          totalCount: topRatedResponse.data?.length || 0,
          generated: new Date().toISOString()
        }
      case RecommendationStrategy.GENRE_BASED:
        const genreResponse = await apiClient.get('/recommendations/for-me', { 
          params: { 
            includeTopRated: false,
            includeGenreBased: true,
            includeAIPowered: false,
            limit: 10
          } 
        })
        return { 
          recommendations: genreResponse.data,
          strategy: RecommendationStrategy.GENRE_BASED,
          totalCount: genreResponse.data?.length || 0,
          generated: new Date().toISOString()
        }
      default:
        const defaultResponse = await apiClient.get('/recommendations/for-me', { params })
        return { 
          recommendations: defaultResponse.data,
          strategy: RecommendationStrategy.PERSONALIZED,
          totalCount: defaultResponse.data?.length || 0,
          generated: new Date().toISOString()
        }
    }
  },

  // Get top-rated books recommendations
  getTopRatedRecommendations: async (params?: RecommendationRequest): Promise<RecommendationResponse> => {
    const response = await apiClient.get('/recommendations/top-rated', { params })
    return { 
      recommendations: response.data,
      strategy: RecommendationStrategy.TOP_RATED,
      totalCount: response.data?.length || 0,
      generated: new Date().toISOString()
    }
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
    return { 
      recommendations: response.data,
      strategy: RecommendationStrategy.GENRE_BASED,
      totalCount: response.data?.length || 0,
      generated: new Date().toISOString()
    }
  },

  // Get trending books recommendations (using top-rated as fallback)
  getTrendingRecommendations: async (params?: RecommendationRequest): Promise<RecommendationResponse> => {
    // Use top-rated endpoint as trending fallback
    const response = await apiClient.get('/recommendations/top-rated', { params })
    return { 
      recommendations: response.data,
      strategy: RecommendationStrategy.TRENDING,
      totalCount: response.data?.length || 0,
      generated: new Date().toISOString()
    }
  },

  // Refresh recommendations (just refetch for now)
  refreshRecommendations: async (): Promise<RecommendationResponse> => {
    // For now, just return empty - the hooks will refetch automatically
    return { 
      recommendations: [],
      strategy: RecommendationStrategy.PERSONALIZED,
      totalCount: 0,
      generated: new Date().toISOString()
    }
  },

  // Get personalized recommendations for a specific user
  getPersonalizedRecommendations: async (userId: string, filters?: any): Promise<RecommendationResponse> => {
    const response = await apiClient.get('/recommendations/for-me', { 
      params: { 
        userId,
        ...filters
      } 
    })
    return { 
      recommendations: response.data,
      strategy: RecommendationStrategy.PERSONALIZED,
      totalCount: response.data?.length || 0,
      generated: new Date().toISOString()
    }
  },

  // Provide feedback on a recommendation (placeholder)
  provideFeedback: async (bookId: string, helpful: boolean, reason?: string): Promise<void> => {
    // Placeholder - no backend endpoint yet
    console.log('Feedback provided:', { bookId, helpful, reason })
  },
}
