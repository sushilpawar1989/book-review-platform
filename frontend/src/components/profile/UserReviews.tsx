import { useState, useEffect } from 'react'
import { MessageSquare, BookOpen, Star, ExternalLink, Loader2 } from 'lucide-react'
import { reviewsApi } from '@/api/reviews'
import { Review } from '@/types/review'
import { formatDistanceToNow } from 'date-fns'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Link } from 'react-router-dom'
import { toast } from '@/hooks/use-toast'

interface UserReviewsProps {
  className?: string
}

const REVIEWS_PER_PAGE = 5

export default function UserReviews({ className }: UserReviewsProps) {
  const [reviews, setReviews] = useState<Review[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [isError, setIsError] = useState(false)
  const [currentPage, setCurrentPage] = useState(1)
  const [totalPages, setTotalPages] = useState(0)
  const [totalReviews, setTotalReviews] = useState(0)

  // Load user's reviews
  const loadReviews = async (page: number = 1) => {
    try {
      setIsLoading(true)
      setIsError(false)
      
      const response = await reviewsApi.getCurrentUserReviews({
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
      console.error('Error loading user reviews:', error)
      setIsError(true)
      toast({
        title: 'Error',
        description: 'Failed to load your reviews. Please try again.',
        variant: 'destructive',
      })
    } finally {
      setIsLoading(false)
    }
  }

  useEffect(() => {
    loadReviews()
  }, [])

  const handlePageChange = (page: number) => {
    loadReviews(page)
  }

  const renderStars = (rating: number) => {
    return Array.from({ length: 5 }, (_, i) => (
      <Star
        key={i}
        className={`h-4 w-4 ${
          i < rating
            ? 'fill-yellow-400 text-yellow-400'
            : 'text-gray-300'
        }`}
      />
    ))
  }

  if (isLoading) {
    return (
      <Card className={className}>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <MessageSquare className="h-5 w-5" />
            My Reviews
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex items-center justify-center py-8">
            <div className="text-center">
              <Loader2 className="h-6 w-6 animate-spin mx-auto mb-2" />
              <p className="text-sm text-muted-foreground">Loading reviews...</p>
            </div>
          </div>
        </CardContent>
      </Card>
    )
  }

  return (
    <Card className={className}>
      <CardHeader>
        <div className="flex items-center justify-between">
          <CardTitle className="flex items-center gap-2">
            <MessageSquare className="h-5 w-5" />
            My Reviews ({totalReviews})
          </CardTitle>
        </div>
      </CardHeader>
      <CardContent>
        {isError ? (
          <div className="text-center py-8">
            <MessageSquare className="h-12 w-12 mx-auto mb-4 text-gray-400" />
            <h3 className="text-lg font-semibold mb-2">Error Loading Reviews</h3>
            <p className="text-muted-foreground">
              Something went wrong while loading your reviews.
            </p>
            <Button onClick={() => loadReviews(1)} variant="outline" className="mt-4">
              Try Again
            </Button>
          </div>
        ) : reviews.length === 0 ? (
          <div className="text-center py-8">
            <MessageSquare className="h-12 w-12 mx-auto mb-4 text-gray-400" />
            <h3 className="text-lg font-semibold mb-2">No Reviews Yet</h3>
            <p className="text-muted-foreground mb-4">
              You haven't written any reviews yet. Start by exploring some books!
            </p>
            <Button asChild>
              <Link to="/books">
                <BookOpen className="mr-2 h-4 w-4" />
                Browse Books
              </Link>
            </Button>
          </div>
        ) : (
          <div className="space-y-4">
            {/* Reviews List */}
            {reviews.map((review) => (
              <div
                key={review.id}
                className="border rounded-lg p-4 hover:bg-gray-50 transition-colors"
              >
                <div className="flex items-start justify-between">
                  <div className="flex-1">
                    {/* Book Info */}
                    <div className="flex items-center gap-2 mb-2">
                      <Link
                        to={`/books/${review.bookId}`}
                        className="font-medium text-blue-600 hover:text-blue-800 hover:underline flex items-center gap-1"
                      >
                        <BookOpen className="h-4 w-4" />
                        {review.bookTitle}
                      </Link>
                      <ExternalLink className="h-4 w-4 text-gray-400" />
                    </div>

                    {/* Rating */}
                    <div className="flex items-center gap-2 mb-2">
                      <div className="flex items-center">
                        {renderStars(review.rating)}
                      </div>
                      <span className="text-sm font-medium text-gray-700">
                        {review.rating}/5
                      </span>
                    </div>

                    {/* Review Text */}
                    <p className="text-gray-700 leading-relaxed mb-3 line-clamp-3">
                      {review.text}
                    </p>

                    {/* Review Meta */}
                    <div className="flex items-center justify-between text-sm text-gray-500">
                      <span>
                        {formatDistanceToNow(new Date(review.createdAt), { addSuffix: true })}
                      </span>
                      {review.updatedAt !== review.createdAt && (
                        <span className="text-xs">• edited</span>
                      )}
                    </div>
                  </div>

                  {/* Action Button */}
                  <div className="ml-4">
                    <Button asChild variant="outline" size="sm">
                      <Link to={`/books/${review.bookId}`}>
                        View Book
                      </Link>
                    </Button>
                  </div>
                </div>
              </div>
            ))}

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

            {/* Results Summary */}
            {totalReviews > 0 && (
              <div className="text-center text-sm text-muted-foreground pt-4 border-t">
                Showing {Math.min(currentPage * REVIEWS_PER_PAGE, totalReviews)} of {totalReviews} reviews
              </div>
            )}
          </div>
        )}
      </CardContent>
    </Card>
  )
}
