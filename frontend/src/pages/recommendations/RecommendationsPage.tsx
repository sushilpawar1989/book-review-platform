import { useState } from 'react'
import { Sparkles, TrendingUp, Award, Users, RefreshCw, Loader2, BookOpen } from 'lucide-react'
import { RecommendationStrategy } from '@/types/recommendation'
import { useRecommendations, useRefreshRecommendations } from '@/hooks/useRecommendations'
import { useAuth } from '@/context/AuthContext'
import RecommendationSection from '@/components/recommendations/RecommendationSection'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'

export default function RecommendationsPage() {
  const { user } = useAuth()
  const refreshMutation = useRefreshRecommendations()

  // Fetch general personalized recommendations
  const {
    data: personalizedRecs,
    isLoading: isLoadingPersonalized,
    isError: isPersonalizedError,
  } = useRecommendations({
    limit: 12,
    includeTopRated: true,
    includeGenreBased: true,
    excludeAlreadyReviewed: true,
  })

  const handleRefreshAll = () => {
    refreshMutation.mutate()
  }

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900 mb-2">
            Recommendations for You
          </h1>
          <p className="text-gray-600">
            Discover your next favorite book with personalized recommendations
          </p>
        </div>
        <Button 
          variant="outline" 
          onClick={handleRefreshAll}
          disabled={refreshMutation.isPending}
        >
          {refreshMutation.isPending ? (
            <>
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              Refreshing...
            </>
          ) : (
            <>
              <RefreshCw className="mr-2 h-4 w-4" />
              Refresh All
            </>
          )}
        </Button>
      </div>

      {/* Personalized Overview */}
      {isLoadingPersonalized ? (
        <Card>
          <CardContent className="p-8">
            <div className="flex items-center justify-center">
              <div className="text-center">
                <Loader2 className="h-8 w-8 animate-spin mx-auto mb-4" />
                <p className="text-muted-foreground">Loading your personalized recommendations...</p>
              </div>
            </div>
          </CardContent>
        </Card>
      ) : isPersonalizedError ? (
        <Card>
          <CardContent className="p-8 text-center">
            <BookOpen className="h-12 w-12 mx-auto mb-4 text-gray-400" />
            <h3 className="text-lg font-semibold mb-2">Unable to Load Recommendations</h3>
            <p className="text-muted-foreground mb-4">
              We're having trouble generating your recommendations. Please try again.
            </p>
            <Button onClick={handleRefreshAll}>Try Again</Button>
          </CardContent>
        </Card>
      ) : personalizedRecs && personalizedRecs.recommendations.length > 0 ? (
        <Card>
          <CardContent className="p-6">
            <div className="flex items-center gap-3 mb-4">
              <Sparkles className="h-6 w-6 text-purple-600" />
              <div>
                <h2 className="text-xl font-semibold">Your Personalized Picks</h2>
                <p className="text-sm text-muted-foreground">
                  Based on your reading history and preferences
                </p>
              </div>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6 gap-4">
              {personalizedRecs.recommendations.slice(0, 6).map((recommendedBook) => (
                <div
                  key={recommendedBook.book.id}
                  className="group cursor-pointer"
                >
                  <div className="aspect-[3/4] relative overflow-hidden rounded-lg mb-3">
                    {recommendedBook.book.coverImageUrl ? (
                      <img
                        src={recommendedBook.book.coverImageUrl}
                        alt={`Cover of ${recommendedBook.book.title}`}
                        className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-200"
                      />
                    ) : (
                      <div className="w-full h-full bg-gradient-to-br from-gray-100 to-gray-200 flex items-center justify-center">
                        <BookOpen className="h-8 w-8 text-gray-400" />
                      </div>
                    )}
                  </div>
                  <h4 className="font-medium text-sm line-clamp-2 mb-1">
                    {recommendedBook.book.title}
                  </h4>
                  <p className="text-xs text-muted-foreground">
                    by {recommendedBook.book.author}
                  </p>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      ) : null}

      {/* Recommendation Sections */}
      <div className="space-y-8">
        {/* Top Rated Books */}
        <RecommendationSection
          strategy={RecommendationStrategy.TOP_RATED}
          title="Top Rated Books"
          description="Highest rated books across all genres"
          icon={<Award className="h-6 w-6 text-yellow-600" />}
          defaultLimit={6}
        />

        {/* Genre-Based Recommendations */}
        <RecommendationSection
          strategy={RecommendationStrategy.GENRE_BASED}
          title="Based on Your Favorites"
          description="Books from genres you love"
          icon={<Sparkles className="h-6 w-6 text-blue-600" />}
          defaultLimit={6}
        />

        {/* Trending Books */}
        <RecommendationSection
          strategy={RecommendationStrategy.TRENDING}
          title="Trending Now"
          description="Popular books this month"
          icon={<TrendingUp className="h-6 w-6 text-green-600" />}
          defaultLimit={6}
        />

        {/* Similar Users Recommendations */}
        <RecommendationSection
          strategy={RecommendationStrategy.SIMILAR_USERS}
          title="Readers Like You Enjoyed"
          description="Recommendations from users with similar tastes"
          icon={<Users className="h-6 w-6 text-purple-600" />}
          defaultLimit={6}
        />
      </div>

      {/* Empty State for New Users */}
      {!isLoadingPersonalized && 
       !isPersonalizedError && 
       (!personalizedRecs || personalizedRecs.recommendations.length === 0) && (
        <Card>
          <CardContent className="p-8 text-center">
            <Sparkles className="h-12 w-12 mx-auto mb-4 text-gray-400" />
            <h3 className="text-lg font-semibold mb-2">Start Building Your Recommendations</h3>
            <p className="text-muted-foreground mb-4">
              Rate some books and mark your favorites to get personalized recommendations!
            </p>
            <Button asChild>
              <a href="/books">Discover Books</a>
            </Button>
          </CardContent>
        </Card>
      )}
    </div>
  )
}
