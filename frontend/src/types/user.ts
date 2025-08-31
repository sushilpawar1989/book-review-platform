export interface UserProfile {
  id: string
  email: string
  firstName: string
  lastName: string
  role: 'USER' | 'ADMIN'
  bio?: string
  preferredGenres: string[]
  createdAt: string
  updatedAt?: string
  totalReviews: number
  totalFavoriteBooks: number
  averageRating: number
  recentReviews?: ReviewDTO[]
}

export interface UserStats {
  totalReviews: number
  totalFavoriteBooks: number
  averageRating: number
  genreDistribution: Record<string, number>
  reviewsThisMonth: number
  favoriteGenres: string[]
}

export interface ReviewDTO {
  id: string
  bookId: string
  rating: number
  reviewText: string
  createdAt: string
  updatedAt: string
}

export interface UpdateProfileRequest {
  firstName?: string
  lastName?: string
  bio?: string
  preferredGenres?: string[]
}

export interface FavoriteBook {
  id: string
  title: string
  author: string
  description: string
  coverImageUrl?: string
  genres: string[]
  publishedYear: number
  averageRating: number
  totalReviews: number
  addedToFavoritesAt: string
  hasUserReviewed: boolean
}

export interface FavoriteToggleRequest {
  bookId: string
}

export interface FavoriteToggleResponse {
  isFavorite: boolean
  message: string
}
