import { useState } from 'react'
import { ChevronRight, RefreshCw, Loader2 } from 'lucide-react'
import { RecommendationStrategy, RecommendationRequest } from '@/types/recommendation'
import { useRecommendationsByGenre, useTopRatedRecommendations, useRecommendations } from '@/hooks/useRecommendations'
import RecommendedBookCard from './RecommendedBookCard'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'

interface RecommendationSectionProps {
  strategy: RecommendationStrategy
  title: string
  description: string
  icon: React.ReactNode
  defaultLimit?: number
  showViewAll?: boolean
  className?: string
}

export default function RecommendationSection({
  strategy,
  title,
  description,
  icon,
  defaultLimit = 6,
  showViewAll = true,
  className
}: RecommendationSectionProps) {
  const [expanded, setExpanded] = useState(false)
  const [params] = useState<RecommendationRequest>({
    limit: expanded ? 20 : defaultLimit,
    excludeAlreadyReviewed: true,
  })

  // Use appropriate hook based on strategy
  const getRecommendationHook = () => {
    switch (strategy) {
      case RecommendationStrategy.TOP_RATED:
        return useTopRatedRecommendations(params.limit)
      case RecommendationStrategy.GENRE_BASED:
        return useRecommendationsByGenre([]) // Will be improved later
      default:
        return useRecommendations(params)
    }
  }

  const {
    data: recommendations,
    isLoading,
    isError,
    error,
    refetch,
    isFetching,
  } = getRecommendationHook()

  const books = recommendations?.recommendations || []
  const hasMore = recommendations && recommendations.totalCount > books.length

  const handleRefresh = () => {
    refetch()
  }

  const handleViewAll = () => {
    setExpanded(!expanded)
  }

  if (isError) {
    return (
      <Card className={className}>
        <CardHeader>
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              {icon}
              <div>
                <CardTitle className="text-xl">{title}</CardTitle>
                <p className="text-sm text-muted-foreground">{description}</p>
              </div>
            </div>
            <Button variant="outline" size="sm" onClick={handleRefresh}>
              <RefreshCw className="h-4 w-4" />
            </Button>
          </div>
        </CardHeader>
        <CardContent>
          <div className="text-center py-8">
            <p className="text-muted-foreground mb-4">
              {error instanceof Error ? error.message : 'Failed to load recommendations'}
            </p>
            <Button variant="outline" onClick={handleRefresh}>
              Try Again
            </Button>
          </div>
        </CardContent>
      </Card>
    )
  }

  return (
    <Card className={className}>
      <CardHeader>
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            {icon}
            <div>
              <CardTitle className="text-xl">{title}</CardTitle>
              <p className="text-sm text-muted-foreground">{description}</p>
            </div>
          </div>
          <div className="flex items-center gap-2">
            <Button
              variant="ghost"
              size="sm"
              onClick={handleRefresh}
              disabled={isFetching}
            >
              {isFetching ? (
                <Loader2 className="h-4 w-4 animate-spin" />
              ) : (
                <RefreshCw className="h-4 w-4" />
              )}
            </Button>
            {showViewAll && hasMore && (
              <Button variant="outline" size="sm" onClick={handleViewAll}>
                {expanded ? 'Show Less' : 'View All'}
                <ChevronRight 
                  className={`h-4 w-4 ml-1 transition-transform ${
                    expanded ? 'rotate-90' : ''
                  }`} 
                />
              </Button>
            )}
          </div>
        </div>
      </CardHeader>
      <CardContent>
        {isLoading ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6 gap-4">
            {Array.from({ length: defaultLimit }, (_, i) => (
              <div key={i} className="animate-pulse">
                <div className="aspect-[3/4] bg-gray-200 rounded-lg mb-3"></div>
                <div className="space-y-2">
                  <div className="h-4 bg-gray-200 rounded w-3/4"></div>
                  <div className="h-3 bg-gray-200 rounded w-1/2"></div>
                  <div className="h-3 bg-gray-200 rounded w-2/3"></div>
                </div>
              </div>
            ))}
          </div>
        ) : books.length === 0 ? (
          <div className="text-center py-8">
            <p className="text-muted-foreground mb-4">
              No recommendations available for this category.
            </p>
            <Button variant="outline" onClick={handleRefresh}>
              <RefreshCw className="h-4 w-4 mr-2" />
              Refresh
            </Button>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6 gap-4">
            {books.map((recommendedBook) => (
              <RecommendedBookCard
                key={recommendedBook.book.id}
                recommendedBook={recommendedBook}
                showFeedback={true}
              />
            ))}
          </div>
        )}

        {books.length > 0 && recommendations && (
          <div className="mt-6 text-center text-sm text-muted-foreground">
            Showing {books.length} of {recommendations.totalCount} recommendations
          </div>
        )}
      </CardContent>
    </Card>
  )
}
