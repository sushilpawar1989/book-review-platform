import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { reviewsApi } from '@/api/reviews'
import { Review, CreateReviewRequest, UpdateReviewRequest, ReviewStats } from '@/types/review'
import { PaginationParams } from '@/types/book'

// Hook to fetch reviews for a specific book
export const useBookReviews = (bookId: string, params?: PaginationParams) => {
  return useQuery({
    queryKey: ['reviews', 'book', bookId, params],
    queryFn: () => reviewsApi.getReviewsByBook(bookId, params),
    staleTime: 0, // Always fetch fresh data
    gcTime: 0, // Don't cache
    refetchOnMount: true,
    refetchOnWindowFocus: true,
  })
}

// Hook to fetch a single review
export const useReview = (reviewId: string) => {
  return useQuery({
    queryKey: ['review', reviewId],
    queryFn: () => reviewsApi.getReviewById(reviewId),
    staleTime: 0, // Always fetch fresh data
    gcTime: 0, // Don't cache
    refetchOnMount: true,
    refetchOnWindowFocus: true,
    enabled: !!reviewId,
  })
}

// Hook to fetch reviews by a specific user
export const useUserReviews = (params?: PaginationParams & { userId?: string }) => {
  return useQuery({
    queryKey: ['reviews', 'user', params],
    queryFn: () => reviewsApi.getReviews(params),
    staleTime: 0, // Always fetch fresh data
    gcTime: 0, // Don't cache
    refetchOnMount: true,
    refetchOnWindowFocus: true,
  })
}

// Hook to fetch current user's review for a specific book
export const useCurrentUserBookReview = (bookId: string) => {
  return useQuery({
    queryKey: ['reviews', 'currentUser', 'book', bookId],
    queryFn: () => reviewsApi.getCurrentUserReviewForBook(bookId),
    staleTime: 0, // Always fetch fresh data
    gcTime: 0, // Don't cache
    refetchOnMount: true,
    refetchOnWindowFocus: true,
    enabled: !!bookId,
  })
}

// Hook to fetch review statistics for a book
export const useBookReviewStats = (bookId: string) => {
  return useQuery({
    queryKey: ['reviews', 'stats', bookId],
    queryFn: () => reviewsApi.getReviewsByBook(bookId, { size: 1000 }), // Get all reviews to calculate stats
    staleTime: 0, // Always fetch fresh data
    gcTime: 0, // Don't cache
    refetchOnMount: true,
    refetchOnWindowFocus: true,
    enabled: !!bookId,
    select: (data) => {
      // Calculate stats from reviews data
      const reviews = data.content || []
      const totalReviews = reviews.length
      const averageRating = totalReviews > 0 
        ? reviews.reduce((sum, review) => sum + review.rating, 0) / totalReviews 
        : 0
      
      const ratingDistribution: Record<number, number> = { 1: 0, 2: 0, 3: 0, 4: 0, 5: 0 }
      reviews.forEach(review => {
        ratingDistribution[review.rating] = (ratingDistribution[review.rating] || 0) + 1
      })
      
      return {
        totalReviews,
        averageRating,
        ratingDistribution
      } as ReviewStats
    }
  })
}

// Hook to create a new review
export const useCreateReview = () => {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: reviewsApi.createReview,
    onSuccess: (data, variables) => {
      // Invalidate related queries
      queryClient.invalidateQueries({ queryKey: ['reviews', 'book', variables.bookId] })
      queryClient.invalidateQueries({ queryKey: ['reviews', 'currentUser'] })
      queryClient.invalidateQueries({ queryKey: ['book', variables.bookId] })
      // Refresh user profile to update review count and average rating
      queryClient.refetchQueries({ queryKey: ['user', 'profile'] })
    },
  })
}

// Hook to update an existing review
export const useUpdateReview = () => {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: ({ id, data }: { id: string; data: UpdateReviewRequest }) => 
      reviewsApi.updateReview(id, data),
    onSuccess: (data, variables) => {
      // Invalidate related queries
      queryClient.invalidateQueries({ queryKey: ['reviews', 'book', data.bookId] })
      queryClient.invalidateQueries({ queryKey: ['reviews', 'currentUser'] })
      queryClient.invalidateQueries({ queryKey: ['book', data.bookId] })
      // Refresh user profile to update review count and average rating
      queryClient.refetchQueries({ queryKey: ['user', 'profile'] })
    },
  })
}

// Hook to delete a review
export const useDeleteReview = () => {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: reviewsApi.deleteReview,
    onSuccess: (data, reviewId) => {
      // Invalidate related queries
      queryClient.invalidateQueries({ queryKey: ['reviews'] })
      queryClient.invalidateQueries({ queryKey: ['reviews', 'currentUser'] })
      // Refresh user profile to update review count and average rating
      queryClient.refetchQueries({ queryKey: ['user', 'profile'] })
      // Note: We can't invalidate book-specific queries without knowing the bookId
      // This will be handled by the component calling this hook
    },
  })
}
