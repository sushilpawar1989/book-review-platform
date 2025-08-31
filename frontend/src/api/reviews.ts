import { apiClient } from './client'
import { Review, CreateReviewRequest, UpdateReviewRequest } from '@/types/review'
import { PaginatedResponse, PaginationParams } from '@/types/book'

export const reviewsApi = {
  // Get all reviews with pagination and filtering
  getReviews: async (params?: PaginationParams & {
    bookId?: string
    userId?: string
    rating?: number
    minRating?: number
  }): Promise<PaginatedResponse<Review>> => {
    const response = await apiClient.get('/reviews', { params })
    return response.data
  },

  // Get review by ID
  getReviewById: async (id: string): Promise<Review> => {
    const response = await apiClient.get(`/reviews/${id}`)
    return response.data
  },

  // Create a new review
  createReview: async (data: CreateReviewRequest): Promise<Review> => {
    const response = await apiClient.post('/reviews', {
      bookId: parseInt(data.bookId),
      rating: data.rating,
      text: data.text
    })
    return response.data
  },

  // Update an existing review
  updateReview: async (id: string, data: UpdateReviewRequest): Promise<Review> => {
    const response = await apiClient.put(`/reviews/${id}`, {
      rating: data.rating,
      text: data.text
    })
    return response.data
  },

  // Delete a review
  deleteReview: async (id: string): Promise<void> => {
    await apiClient.delete(`/reviews/${id}`)
  },

  // Get reviews for a specific book
  getReviewsByBook: async (bookId: string, params?: PaginationParams): Promise<PaginatedResponse<Review>> => {
    const response = await apiClient.get(`/reviews/book/${bookId}`, { params })
    return response.data
  },

  // Get reviews by a specific user
  getReviewsByUser: async (userId: string, params?: PaginationParams): Promise<PaginatedResponse<Review>> => {
    const response = await apiClient.get(`/reviews/user/${userId}`, { params })
    return response.data
  },

  // Get current user's reviews
  getCurrentUserReviews: async (params?: PaginationParams): Promise<PaginatedResponse<Review>> => {
    const response = await apiClient.get('/reviews/my-reviews', { params })
    return response.data
  },

  // Get current user's review for a specific book
  getCurrentUserReviewForBook: async (bookId: string): Promise<Review | null> => {
    try {
      const response = await apiClient.get(`/reviews/my-review/book/${bookId}`)
      return response.data
    } catch (error: any) {
      if (error.response?.status === 404) {
        return null // User hasn't reviewed this book
      }
      throw error
    }
  },

  // Check if current user has reviewed a book
  hasCurrentUserReviewedBook: async (bookId: string): Promise<boolean> => {
    const response = await apiClient.get(`/reviews/has-reviewed/book/${bookId}`)
    return response.data
  }
}
