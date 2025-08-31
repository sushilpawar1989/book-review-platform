import { Link } from 'react-router-dom'
import { Star, BookOpen, ThumbsUp, ThumbsDown, Sparkles } from 'lucide-react'
import { RecommendedBook, RecommendationStrategy } from '@/types/recommendation'
import { useRefreshRecommendations } from '@/hooks/useRecommendations'
import { Card, CardContent } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import FavoriteButton from '@/components/profile/FavoriteButton'

interface RecommendedBookCardProps {
  recommendedBook: RecommendedBook
  showFeedback?: boolean
  className?: string
}

export default function RecommendedBookCard({ 
  recommendedBook, 
  showFeedback = true,
  className 
}: RecommendedBookCardProps) {
  const { book, score, reason, strategy } = recommendedBook
  const refreshMutation = useRefreshRecommendations()

  const handleFeedback = (helpful: boolean) => {
    // For now, just refresh recommendations
    refreshMutation.mutate()
  }

  const formatGenres = (genres: string[]) => {
    return genres.slice(0, 2).map(genre => 
      genre.toLowerCase().replace('_', ' ').replace(/\b\w/g, l => l.toUpperCase())
    )
  }

  const renderStars = (rating: number) => {
    const stars = []
    const fullStars = Math.floor(rating)
    const hasHalfStar = rating % 1 !== 0

    for (let i = 0; i < fullStars; i++) {
      stars.push(
        <Star key={i} className="h-4 w-4 fill-yellow-400 text-yellow-400" />
      )
    }

    if (hasHalfStar) {
      stars.push(
        <Star key="half" className="h-4 w-4 fill-yellow-400/50 text-yellow-400" />
      )
    }

    const emptyStars = 5 - Math.ceil(rating)
    for (let i = 0; i < emptyStars; i++) {
      stars.push(
        <Star key={`empty-${i}`} className="h-4 w-4 text-gray-300" />
      )
    }

    return stars
  }

  const getStrategyInfo = (strategy: RecommendationStrategy) => {
    switch (strategy) {
      case RecommendationStrategy.TOP_RATED:
        return { label: 'Top Rated', color: 'bg-yellow-100 text-yellow-800' }
      case RecommendationStrategy.GENRE_BASED:
        return { label: 'Your Genres', color: 'bg-blue-100 text-blue-800' }
      case RecommendationStrategy.SIMILAR_USERS:
        return { label: 'Similar Readers', color: 'bg-green-100 text-green-800' }
      case RecommendationStrategy.TRENDING:
        return { label: 'Trending', color: 'bg-purple-100 text-purple-800' }
      case RecommendationStrategy.PERSONALIZED:
        return { label: 'For You', color: 'bg-pink-100 text-pink-800' }
      default:
        return { label: 'Recommended', color: 'bg-gray-100 text-gray-800' }
    }
  }

  const strategyInfo = getStrategyInfo(strategy)

  return (
    <Card className={`group hover:shadow-lg transition-all duration-200 ${className}`}>
      <div className="relative">
        {/* Strategy Badge */}
        <div className="absolute top-2 left-2 z-10">
          <Badge className={`text-xs ${strategyInfo.color}`}>
            <Sparkles className="h-3 w-3 mr-1" />
            {strategyInfo.label}
          </Badge>
        </div>

        {/* Score Badge */}
        {score > 0 && (
          <div className="absolute top-2 right-2 bg-black/75 text-white px-2 py-1 rounded-md text-xs font-medium z-10">
            {Math.round(score * 100)}%
          </div>
        )}

        <Link to={`/books/${book.id}`} className="block">
          <div className="relative aspect-[3/4] overflow-hidden rounded-t-lg">
            {book.coverImageUrl ? (
              <img
                src={book.coverImageUrl}
                alt={`Cover of ${book.title}`}
                className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-200"
                loading="lazy"
              />
            ) : (
              <div className="w-full h-full bg-gradient-to-br from-gray-100 to-gray-200 flex items-center justify-center">
                <BookOpen className="h-12 w-12 text-gray-400" />
              </div>
            )}
          </div>
        </Link>

        <CardContent className="p-4">
          <div className="space-y-3">
            {/* Title and Author */}
            <Link to={`/books/${book.id}`} className="block">
              <h3 className="font-semibold text-lg leading-tight line-clamp-2 group-hover:text-primary transition-colors">
                {book.title}
              </h3>
              <p className="text-muted-foreground text-sm mt-1">
                by {book.author}
              </p>
            </Link>

            {/* Rating and Reviews */}
            <div className="flex items-center gap-2">
              <div className="flex items-center">
                {renderStars(book.averageRating)}
              </div>
              <span className="text-sm text-muted-foreground">
                ({book.totalReviews})
              </span>
            </div>

            {/* Genres */}
            {book.genres.length > 0 && (
              <div className="flex gap-1 flex-wrap">
                {formatGenres(book.genres).map((genre, index) => (
                  <Badge key={index} variant="secondary" className="text-xs">
                    {genre}
                  </Badge>
                ))}
                {book.genres.length > 2 && (
                  <Badge variant="outline" className="text-xs">
                    +{book.genres.length - 2}
                  </Badge>
                )}
              </div>
            )}

            {/* Recommendation Reason */}
            {reason && (
              <p className="text-xs text-gray-600 italic line-clamp-2">
                {reason}
              </p>
            )}

            {/* Actions */}
            <div className="flex items-center justify-between pt-2">
              <div className="flex-1">
                <FavoriteButton
                  bookId={book.id}
                  variant="outline"
                  size="sm"
                  showText={false}
                />
              </div>

              {/* Feedback Buttons */}
              {showFeedback && (
                <div className="flex gap-1">
                  <Button
                    variant="ghost"
                    size="sm"
                    className="h-8 w-8 p-0"
                    onClick={() => handleFeedback(true)}
                    disabled={refreshMutation.isPending}
                    title="This recommendation was helpful"
                  >
                    <ThumbsUp className="h-4 w-4" />
                  </Button>
                  <Button
                    variant="ghost"
                    size="sm"
                    className="h-8 w-8 p-0"
                    onClick={() => handleFeedback(false)}
                    disabled={refreshMutation.isPending}
                    title="This recommendation was not helpful"
                  >
                    <ThumbsDown className="h-4 w-4" />
                  </Button>
                </div>
              )}
            </div>
          </div>
        </CardContent>
      </div>
    </Card>
  )
}
