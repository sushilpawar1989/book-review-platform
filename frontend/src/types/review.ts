export interface Review {
  id: string
  bookId: string
  bookTitle: string
  userId: string
  userFirstName: string
  userLastName: string
  rating: number
  text: string
  createdAt: string
  updatedAt: string
}

export interface CreateReviewRequest {
  bookId: string
  rating: number
  text: string
}

export interface UpdateReviewRequest {
  rating: number
  text: string
}

export interface ReviewFilters {
  bookId?: string
  userId?: string
  minRating?: number
  maxRating?: number
  sortBy?: 'rating' | 'created' | 'updated'
  sortOrder?: 'asc' | 'desc'
}

export interface ReviewStats {
  totalReviews: number
  averageRating: number
  ratingDistribution: Record<number, number>
}
