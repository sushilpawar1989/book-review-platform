import { Book } from './book'

export interface RecommendationRequest {
  limit?: number
  includeTopRated?: boolean
  includeGenreBased?: boolean
  includeSimilarUsers?: boolean
  excludeAlreadyReviewed?: boolean
}

export interface RecommendationResponse {
  recommendations: RecommendedBook[]
  strategy: RecommendationStrategy
  totalCount: number
  generated: string
}

export interface RecommendedBook {
  book: Book
  score: number
  reason: string
  strategy: RecommendationStrategy
}

export enum RecommendationStrategy {
  TOP_RATED = 'TOP_RATED',
  GENRE_BASED = 'GENRE_BASED',
  SIMILAR_USERS = 'SIMILAR_USERS',
  TRENDING = 'TRENDING',
  PERSONALIZED = 'PERSONALIZED'
}

export interface RecommendationFilters {
  strategies?: RecommendationStrategy[]
  minRating?: number
  excludeGenres?: string[]
  includeGenres?: string[]
  maxResults?: number
}
