import { Star } from 'lucide-react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'

interface RatingStatsProps {
  bookId: string
  totalReviews: number
  averageRating: number
  className?: string
}

export default function RatingStats({ 
  bookId, 
  totalReviews, 
  averageRating, 
  className 
}: RatingStatsProps) {
  const renderStars = (rating: number) => {
    return Array.from({ length: 5 }, (_, i) => (
      <Star
        key={i}
        className={`h-5 w-5 ${
          i < Math.floor(rating)
            ? 'fill-yellow-400 text-yellow-400'
            : i < rating
            ? 'fill-yellow-400/50 text-yellow-400'
            : 'text-gray-300'
        }`}
      />
    ))
  }

  return (
    <Card className={className}>
      <CardHeader>
        <CardTitle>Rating Overview</CardTitle>
      </CardHeader>
      <CardContent className="space-y-4">
        {/* Overall Rating */}
        <div className="text-center pb-4 border-b">
          <div className="text-3xl font-bold text-gray-900 mb-1">
            {averageRating.toFixed(1)}
          </div>
          <div className="flex items-center justify-center mb-2">
            {renderStars(averageRating)}
          </div>
          <div className="text-sm text-gray-600">
            Based on {totalReviews} {totalReviews === 1 ? 'review' : 'reviews'}
          </div>
        </div>

        {/* Quick Stats */}
        <div className="space-y-3">
          <div className="flex justify-between items-center py-2 border-b">
            <span className="text-sm text-gray-600">Total Reviews:</span>
            <span className="font-semibold">{totalReviews}</span>
          </div>
          
          {totalReviews > 0 && (
            <>
              <div className="flex justify-between items-center py-2 border-b">
                <span className="text-sm text-gray-600">Average Rating:</span>
                <span className="font-semibold">{averageRating.toFixed(1)}/5</span>
              </div>
              
              <div className="flex justify-between items-center py-2">
                <span className="text-sm text-gray-600">Rating Scale:</span>
                <span className="text-xs text-gray-500">1-5 stars</span>
              </div>
            </>
          )}
        </div>

        {/* No Reviews Message */}
        {totalReviews === 0 && (
          <div className="text-center py-4 text-sm text-gray-500">
            No reviews yet. Be the first to rate this book!
          </div>
        )}
      </CardContent>
    </Card>
  )
}
