import { useParams, Link } from 'react-router-dom'
import { ArrowLeft, Loader2, BookOpen, Star, Heart } from 'lucide-react'
import { useBook } from '@/hooks/useBooks'
import { useAuth } from '@/context/AuthContext'
import ReviewsList from '@/components/reviews/ReviewsList'
import RatingStats from '@/components/reviews/RatingStats'
import FavoriteButton from '@/components/profile/FavoriteButton'
import { Button } from '@/components/ui/button'
import { Card, CardContent } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'

export default function BookDetailsPage() {
  const { id } = useParams<{ id: string }>()
  const { isAuthenticated } = useAuth()
  const { data: book, isLoading, isError, error, refetch } = useBook(id!)

  // Refresh book data when reviews change (to update average rating, total reviews)
  const handleReviewChange = () => {
    refetch()
  }

  if (isLoading) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="flex items-center justify-center py-12">
          <div className="text-center">
            <Loader2 className="h-8 w-8 animate-spin mx-auto mb-4" />
            <p className="text-muted-foreground">Loading book details...</p>
          </div>
        </div>
      </div>
    )
  }

  if (isError || !book) {
    return (
      <div className="container mx-auto px-4 py-8">
        <div className="mb-6">
          <Button asChild variant="ghost">
            <Link to="/books">
              <ArrowLeft className="mr-2 h-4 w-4" />
              Back to Books
            </Link>
          </Button>
        </div>
        
        <Card className="p-8 text-center">
          <CardContent>
            <div className="text-red-500 mb-4">
              <BookOpen className="h-12 w-12 mx-auto mb-2" />
              <h3 className="text-lg font-semibold">Book Not Found</h3>
            </div>
            <p className="text-muted-foreground mb-4">
              {error instanceof Error ? error.message : 'The requested book could not be found.'}
            </p>
            <Button asChild variant="outline">
              <Link to="/books">
                Back to Books
              </Link>
            </Button>
          </CardContent>
        </Card>
      </div>
    )
  }

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

  const formatGenres = (genres: string[]) => {
    return genres.map(genre => 
      genre.toLowerCase().replace('_', ' ').replace(/\b\w/g, l => l.toUpperCase())
    )
  }

  return (
    <div className="container mx-auto px-4 py-8 max-w-7xl">
      {/* Back Button */}
      <div className="mb-6">
        <Button asChild variant="ghost">
          <Link to="/books">
            <ArrowLeft className="mr-2 h-4 w-4" />
            Back to Books
          </Link>
        </Button>
      </div>

      <div className="grid lg:grid-cols-4 gap-8">
        {/* Left Column - Book Cover and Rating Stats */}
        <div className="lg:col-span-1 space-y-6">
          {/* Book Cover */}
          <div className="aspect-[3/4] w-full max-w-sm mx-auto lg:max-w-none">
            {book.coverImageUrl ? (
              <img
                src={book.coverImageUrl}
                alt={`Cover of ${book.title}`}
                className="w-full h-full object-cover rounded-lg shadow-lg"
              />
            ) : (
              <div className="w-full h-full bg-gradient-to-br from-gray-100 to-gray-200 rounded-lg shadow-lg flex items-center justify-center">
                <BookOpen className="h-16 w-16 text-gray-400" />
              </div>
            )}
          </div>

          {/* Rating Statistics */}
          <RatingStats
            bookId={book.id}
            totalReviews={book.totalReviews}
            averageRating={book.averageRating}
          />

          {/* Quick Actions */}
          <Card>
            <CardContent className="p-4 space-y-3">
              <FavoriteButton
                bookId={book.id}
                variant="default"
                className="w-full"
                showText={true}
              />
              {!isAuthenticated && (
                <div className="text-xs text-gray-500 text-center">
                  <Link to="/login" className="text-blue-600 hover:underline">
                    Sign in
                  </Link>{' '}
                  to add favorites
                </div>
              )}
            </CardContent>
          </Card>
        </div>

        {/* Right Column - Book Information and Reviews */}
        <div className="lg:col-span-3 space-y-8">
          {/* Book Information */}
          <div className="space-y-6">
            <div>
              <h1 className="text-4xl font-bold text-gray-900 mb-3">
                {book.title}
              </h1>
              <p className="text-xl text-gray-600 mb-4">
                by {book.author}
              </p>

              {/* Rating Summary */}
              <div className="flex items-center gap-4 mb-4">
                <div className="flex items-center gap-2">
                  <div className="flex items-center">
                    {renderStars(book.averageRating)}
                  </div>
                  <span className="text-lg font-semibold text-gray-900">
                    {book.averageRating.toFixed(1)}
                  </span>
                </div>
                <span className="text-gray-600">
                  {book.totalReviews} {book.totalReviews === 1 ? 'review' : 'reviews'}
                </span>
              </div>

              {/* Genres */}
              <div className="flex flex-wrap gap-2 mb-6">
                {formatGenres(book.genres).map((genre, index) => (
                  <Badge key={index} variant="secondary">
                    {genre}
                  </Badge>
                ))}
              </div>
            </div>

            {/* Description */}
            <div>
              <h3 className="text-xl font-semibold mb-3">About this book</h3>
              <p className="text-gray-700 leading-relaxed text-lg">
                {book.description}
              </p>
            </div>

            {/* Book Details */}
            <div className="grid md:grid-cols-2 gap-6 p-6 bg-gray-50 rounded-lg">
              <div>
                <h4 className="font-semibold text-gray-900 mb-2">Publication Details</h4>
                <div className="space-y-2 text-sm">
                  <div className="flex justify-between">
                    <span className="text-gray-600">Published:</span>
                    <span className="font-medium">{book.publishedYear}</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-600">Author:</span>
                    <span className="font-medium">{book.author}</span>
                  </div>
                </div>
              </div>
              <div>
                <h4 className="font-semibold text-gray-900 mb-2">Community Stats</h4>
                <div className="space-y-2 text-sm">
                  <div className="flex justify-between">
                    <span className="text-gray-600">Average Rating:</span>
                    <span className="font-medium">{book.averageRating.toFixed(1)}/5</span>
                  </div>
                  <div className="flex justify-between">
                    <span className="text-gray-600">Total Reviews:</span>
                    <span className="font-medium">{book.totalReviews}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Reviews Section */}
          <div>
            <ReviewsList 
              bookId={book.id} 
              bookTitle={book.title}
              onReviewChange={handleReviewChange}
            />
          </div>
        </div>
      </div>
    </div>
  )
}
