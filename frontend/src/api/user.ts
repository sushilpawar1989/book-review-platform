import { apiClient } from './client'
import { 
  UserProfile, 
  UserStats, 
  UpdateProfileRequest, 
  FavoriteBook, 
  FavoriteToggleRequest, 
  FavoriteToggleResponse 
} from '@/types/user'
import { PaginatedResponse, PaginationParams } from '@/types/book'

export const userApi = {
  // Get current user profile
  getProfile: async (): Promise<UserProfile> => {
    const response = await apiClient.get('/users/my-profile')
    return response.data
  },

  // Get current user profile (alias for consistency with hooks)
  getCurrentUserProfile: async (): Promise<UserProfile> => {
    const response = await apiClient.get('/users/my-profile')
    return response.data
  },

  // Update current user profile
  updateProfile: async (profileData: UpdateProfileRequest): Promise<UserProfile> => {
    const response = await apiClient.put('/users/my-profile', profileData)
    return response.data
  },

  // Get user statistics (using current user for now)
  getUserStats: async (): Promise<UserStats> => {
    // This endpoint might need to be implemented differently
    // For now, we'll create stats from the profile data
    const profile = await userApi.getProfile()
    return {
      totalReviews: profile.totalReviews || 0,
      totalFavoriteBooks: profile.totalFavoriteBooks || 0,
      averageRating: profile.averageRating || 0,
      genreDistribution: {},
      reviewsThisMonth: 0,
      favoriteGenres: profile.preferredGenres || [],
    }
  },

  // Get user's favorite books
  getFavoriteBooks: async (params?: PaginationParams): Promise<PaginatedResponse<FavoriteBook>> => {
    const response = await apiClient.get('/users/my-favorites', { params })
    return response.data
  },

  // Add book to favorites
  addToFavorites: async (request: FavoriteToggleRequest): Promise<FavoriteToggleResponse> => {
    const response = await apiClient.post(`/users/favorites/books/${request.bookId}`)
    return { isFavorite: true, message: 'Book added to favorites' }
  },

  // Remove book from favorites
  removeFromFavorites: async (bookId: string): Promise<FavoriteToggleResponse> => {
    const response = await apiClient.delete(`/users/favorites/books/${bookId}`)
    return { isFavorite: false, message: 'Book removed from favorites' }
  },

  // Check if book is in user's favorites
  isFavorite: async (bookId: string): Promise<{ isFavorite: boolean }> => {
    try {
      console.log('🔍 Checking favorite status for bookId:', bookId)
      const response = await apiClient.get(`/users/favorites/books/${bookId}/check`)
      console.log('📊 Favorite check response:', response.data)
      return { isFavorite: response.data }
    } catch (error: any) {
      console.error('❌ Error checking favorite status:', error)
      console.error('❌ Error response:', error.response?.data)
      console.error('❌ Error status:', error.response?.status)
      return { isFavorite: false }
    }
  },

  // Toggle favorite status (add if not favorite, remove if favorite)
  toggleFavorite: async (request: FavoriteToggleRequest): Promise<FavoriteToggleResponse> => {
    try {
      console.log('🔄 toggleFavorite called for bookId:', request.bookId)
      
      const currentStatus = await userApi.isFavorite(request.bookId)
      console.log('📊 Current favorite status:', currentStatus)
      
      if (currentStatus.isFavorite) {
        // Remove from favorites
        console.log('➖ Removing from favorites...')
        const response = await apiClient.delete(`/users/favorites/books/${request.bookId}`)
        console.log('✅ Remove response:', response.status)
        return { isFavorite: false, message: 'Book removed from favorites' }
      } else {
        // Add to favorites
        console.log('➕ Adding to favorites...')
        const response = await apiClient.post(`/users/favorites/books/${request.bookId}`)
        console.log('✅ Add response:', response.status)
        return { isFavorite: true, message: 'Book added to favorites' }
      }
    } catch (error: any) {
      console.error('❌ Error toggling favorite:', error)
      console.error('❌ Error response:', error.response?.data)
      console.error('❌ Error status:', error.response?.status)
      throw new Error(error.response?.data?.message || 'Failed to update favorites')
    }
  },

  // Change password (to be implemented later)
  changePassword: async (currentPassword: string, newPassword: string): Promise<void> => {
    // This endpoint needs to be implemented in the backend
    throw new Error('Change password not implemented yet')
  },

  // Delete account (to be implemented later)
  deleteAccount: async (password: string): Promise<void> => {
    // This endpoint needs to be implemented in the backend
    throw new Error('Delete account not implemented yet')
  },
}
