import { useState, useEffect } from 'react'
import { MessageSquare, Loader2 } from 'lucide-react'
import { Review, CreateReviewRequest, UpdateReviewRequest } from '@/types/review'
import { reviewsApi } from '@/api/reviews'
import { useAuth } from '@/context/AuthContext'
import ReviewCard from './ReviewCard'
import ReviewForm from './ReviewForm'
import { Button } from '@/components/ui/button'
import { toast } from '@/hooks/use-toast'

interface ReviewsListProps {
  bookId: string
  bookTitle: string
  onReviewChange?: () => void // Callback to refresh book data (e.g., average rating)
}

export default function ReviewsList({ bookId, bookTitle, onReviewChange }: ReviewsListProps) {
  const { user } = useAuth()
  const [reviews, setReviews] = useState<Review[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [isError, setIsError] = useState(false)
  const [showReviewForm, setShowReviewForm] = useState(false)
  const [editingReview, setEditingReview] = useState<Review | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [currentPage, setCurrentPage] = useState(1)
  const [totalPages, setTotalPages] = useState(0)
  const [totalReviews, setTotalReviews] = useState(0)

  const REVIEWS_PER_PAGE = 10

  // Load reviews
  const loadReviews = async (page: number = 1) => {
    try {
      setIsLoading(true)
      setIsError(false)
      
      const response = await reviewsApi.getReviewsByBook(bookId, {
        page: page - 1, // Backend uses 0-based pagination
        size: REVIEWS_PER_PAGE,
        sortBy: 'createdAt',
        sortOrder: 'desc'
      })
      
      setReviews(response.content || [])
      setTotalPages(response.totalPages || 0)
      setTotalReviews(response.totalElements || 0)
      setCurrentPage(page)
    } catch (error) {
      console.error('Error loading reviews:', error)
      setIsError(true)
      toast({
        title: 'Error',
        description: 'Failed to load reviews. Please try again.',
        variant: 'destructive',
      })
    } finally {
      setIsLoading(false)
    }
  }

  // Check if user has already reviewed this book
  const [userHasReviewed, setUserHasReviewed] = useState(false)

  useEffect(() => {
    if (user) {
      checkUserReview()
    }
  }, [user, bookId])

  const checkUserReview = async () => {
    try {
      const hasReviewed = await reviewsApi.hasCurrentUserReviewedBook(bookId)
      setUserHasReviewed(hasReviewed)
    } catch (error) {
      console.error('Error checking user review:', error)
    }
  }

  useEffect(() => {
    loadReviews()
  }, [bookId])

  const handleCreateReview = async (data: CreateReviewRequest) => {
    try {
      setIsSubmitting(true)
      await reviewsApi.createReview(data)
      
      setShowReviewForm(false)
      setUserHasReviewed(true)
      await checkUserReview() // Refresh user review data
      await loadReviews(1) // Refresh reviews list
      onReviewChange?.() // Notify parent to refresh book data
      
      toast({
        title: 'Review Posted',
        description: 'Your review has been successfully posted.',
      })
    } catch (error: any) {
      toast({
        title: 'Error',
        description: error.response?.data?.message || 'Failed to post review. Please try again.',
        variant: 'destructive',
      })
      throw error
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleUpdateReview = async (data: UpdateReviewRequest) => {
    if (!editingReview) return
    
    try {
      setIsSubmitting(true)
      await reviewsApi.updateReview(editingReview.id, data)
      
      setEditingReview(null)
      await checkUserReview() // Refresh user review data
      await loadReviews(currentPage) // Refresh current page
      onReviewChange?.() // Notify parent to refresh book data
      
      toast({
        title: 'Review Updated',
        description: 'Your review has been successfully updated.',
      })
    } catch (error: any) {
      toast({
        title: 'Error',
        description: error.response?.data?.message || 'Failed to update review. Please try again.',
        variant: 'destructive',
      })
      throw error
    } finally {
      setIsSubmitting(false)
    }
  }

  const handleDeleteReview = async (reviewId: string) => {
    try {
      await reviewsApi.deleteReview(reviewId)
      
      setUserHasReviewed(false)
      await loadReviews(currentPage) // Refresh current page
      onReviewChange?.() // Notify parent to refresh book data
      
      toast({
        title: 'Review Deleted',
        description: 'Your review has been successfully deleted.',
      })
    } catch (error: any) {
      toast({
        title: 'Error',
        description: error.response?.data?.message || 'Failed to delete review. Please try again.',
        variant: 'destructive',
      })
      throw error
    }
  }

  const handleEditReview = (review: Review) => {
    setEditingReview(review)
    setShowReviewForm(false)
  }

  const handleCancelEdit = () => {
    setEditingReview(null)
  }

  const handlePageChange = (page: number) => {
    loadReviews(page)
  }

  if (isLoading && reviews.length === 0) {
    return (
      <div className="text-center py-12">
        <Loader2 className="h-8 w-8 animate-spin mx-auto mb-4 text-blue-600" />
        <p className="text-gray-600">Loading reviews...</p>
      </div>
    )
  }

  if (isError && reviews.length === 0) {
    return (
      <div className="text-center py-12">
        <MessageSquare className="h-12 w-12 mx-auto mb-4 text-red-500" />
        <h3 className="text-lg font-semibold text-gray-900 mb-2">Error Loading Reviews</h3>
        <p className="text-gray-600 mb-4">Failed to load reviews. Please try again.</p>
        <Button onClick={() => loadReviews(1)} variant="outline">
          Try Again
        </Button>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <MessageSquare className="h-5 w-5 text-blue-600" />
          <h3 className="text-lg font-semibold text-gray-900">
            Reviews ({totalReviews})
          </h3>
        </div>
        
        {/* Write Review Button */}
        {user && !userHasReviewed && !showReviewForm && (
          <Button onClick={() => setShowReviewForm(true)}>
            Write a Review
          </Button>
        )}
      </div>

      {/* Review Form */}
      {showReviewForm && (
        <ReviewForm
          bookId={bookId}
          bookTitle={bookTitle}
          onSubmit={async (data: CreateReviewRequest | UpdateReviewRequest) => {
            if ('bookId' in data) {
              // This is a CreateReviewRequest
              await handleCreateReview(data as CreateReviewRequest)
            } else {
              // This is an UpdateReviewRequest, but we shouldn't reach here for create form
              console.error('Unexpected UpdateReviewRequest in create form')
            }
          }}
          onCancel={() => setShowReviewForm(false)}
          isSubmitting={isSubmitting}
        />
      )}

      {/* Edit Review Form */}
      {editingReview && (
        <ReviewForm
          bookId={bookId}
          bookTitle={bookTitle}
          existingReview={editingReview}
          onSubmit={async (data: CreateReviewRequest | UpdateReviewRequest) => {
            if (!('bookId' in data)) {
              // This is an UpdateReviewRequest
              await handleUpdateReview(data as UpdateReviewRequest)
            } else {
              // This is a CreateReviewRequest, but we shouldn't reach here for edit form
              console.error('Unexpected CreateReviewRequest in edit form')
            }
          }}
          onCancel={handleCancelEdit}
          isSubmitting={isSubmitting}
        />
      )}

      {/* Reviews List */}
      {reviews.length > 0 ? (
        <div className="space-y-4">
          {reviews.map((review) => (
            <ReviewCard
              key={review.id}
              review={review}
              onEdit={handleEditReview}
              onDelete={handleDeleteReview}
              isEditing={editingReview?.id === review.id}
            />
          ))}
        </div>
      ) : (
        <div className="text-center py-12">
          <MessageSquare className="h-12 w-12 mx-auto mb-4 text-gray-400" />
          <h3 className="text-lg font-semibold text-gray-900 mb-2">No Reviews Yet</h3>
          <p className="text-gray-600 mb-4">
            Be the first to share your thoughts about this book!
          </p>
          {user && !userHasReviewed && (
            <Button onClick={() => setShowReviewForm(true)}>
              Write the First Review
            </Button>
          )}
        </div>
      )}

      {/* Pagination */}
      {totalPages > 1 && (
        <div className="flex items-center justify-center gap-2 pt-6 border-t">
          <Button
            variant="outline"
            size="sm"
            onClick={() => handlePageChange(currentPage - 1)}
            disabled={currentPage === 1}
          >
            Previous
          </Button>
          
          <span className="text-sm text-gray-600">
            Page {currentPage} of {totalPages}
          </span>
          
          <Button
            variant="outline"
            size="sm"
            onClick={() => handlePageChange(currentPage + 1)}
            disabled={currentPage === totalPages}
          >
            Next
          </Button>
        </div>
      )}
    </div>
  )
}
