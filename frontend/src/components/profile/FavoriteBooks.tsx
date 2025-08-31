import { useState } from 'react'
import { useSearchParams, Link } from 'react-router-dom'
import { Heart, BookOpen, Star, Loader2, X } from 'lucide-react'
import { useUserFavoriteBooks, useToggleFavorite } from '@/hooks/useUser'
import { formatDistanceToNow } from 'date-fns'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import BookPagination from '@/components/books/BookPagination'
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog'

interface FavoriteBooksProps {
  className?: string
}

const FAVORITES_PER_PAGE = 6

export default function FavoriteBooks({ className }: FavoriteBooksProps) {
  const [searchParams, setSearchParams] = useSearchParams()
  const [bookToRemove, setBookToRemove] = useState<string | null>(null)
  
  // Get URL parameters
  const page = parseInt(searchParams.get('favoritePage') || '1', 10)

  // Fetch user's favorite books
  const {
    data: favoritesData,
    isLoading,
    isError,
    error,
  } = useUserFavoriteBooks({
    page: page - 1, // Backend uses 0-based indexing
    size: FAVORITES_PER_PAGE,
  })

  const toggleFavoriteMutation = useToggleFavorite()

  const favorites = favoritesData?.content || []
  const totalPages = favoritesData?.totalPages || 0
  const totalFavorites = favoritesData?.totalElements || 0

  const handlePageChange = (newPage: number) => {
    const newParams = new URLSearchParams(searchParams)
    newParams.set('favoritePage', newPage.toString())
    setSearchParams(newParams)
  }

  const handleRemoveClick = (bookId: string) => {
    setBookToRemove(bookId)
  }

  const handleConfirmRemove = () => {
    if (bookToRemove) {
      toggleFavoriteMutation.mutate({ bookId: bookToRemove })
      setBookToRemove(null)
    }
  }

  const handleCancelRemove = () => {
    setBookToRemove(null)
  }

  const renderStars = (rating: number) => {
    return Array.from({ length: 5 }, (_, i) => (
      <Star
        key={i}
        className={`h-4 w-4 ${
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
    return genres.slice(0, 2).map(genre => 
      genre.toLowerCase().replace('_', ' ').replace(/\b\w/g, l => l.toUpperCase())
    )
  }

  if (isLoading) {
    return (
      <Card className={className}>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Heart className="h-5 w-5" />
            My Favorite Books
          </CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex items-center justify-center py-8">
            <div className="text-center">
              <Loader2 className="h-6 w-6 animate-spin mx-auto mb-2" />
              <p className="text-sm text-muted-foreground">Loading favorites...</p>
            </div>
          </div>
        </CardContent>
      </Card>
    )
  }

  return (
    <>
      <Card className={className}>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Heart className="h-5 w-5" />
            My Favorite Books ({totalFavorites})
          </CardTitle>
        </CardHeader>
        <CardContent>
          {isError ? (
            <div className="text-center py-8">
              <Heart className="h-12 w-12 mx-auto mb-4 text-gray-400" />
              <h3 className="text-lg font-semibold mb-2">Error Loading Favorites</h3>
              <p className="text-muted-foreground">
                {error instanceof Error ? error.message : 'Something went wrong while loading your favorite books.'}
              </p>
            </div>
          ) : favorites.length === 0 ? (
            <div className="text-center py-8">
              <Heart className="h-12 w-12 mx-auto mb-4 text-gray-400" />
              <h3 className="text-lg font-semibold mb-2">No Favorite Books Yet</h3>
              <p className="text-muted-foreground mb-4">
                Start building your collection by adding books to your favorites!
              </p>
              <Button asChild>
                <Link to="/books">
                  <BookOpen className="mr-2 h-4 w-4" />
                  Discover Books
                </Link>
              </Button>
            </div>
          ) : (
            <div className="space-y-4">
              {/* Favorites Grid */}
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {favorites.map((favorite) => (
                  <div
                    key={favorite.id}
                    className="border rounded-lg p-4 hover:shadow-md transition-all duration-200 relative group"
                  >
                    {/* Remove Button */}
                    <Button
                      variant="ghost"
                      size="sm"
                      className="absolute top-2 right-2 h-8 w-8 p-0 opacity-0 group-hover:opacity-100 transition-opacity bg-white/80 hover:bg-white"
                      onClick={() => handleRemoveClick(favorite.id)}
                    >
                      <X className="h-4 w-4" />
                    </Button>

                    {/* Book Cover */}
                    <Link to={`/books/${favorite.id}`} className="block">
                      <div className="aspect-[3/4] w-full mb-3 relative overflow-hidden rounded">
                        {favorite.coverImageUrl ? (
                          <img
                            src={favorite.coverImageUrl}
                            alt={`Cover of ${favorite.title}`}
                            className="w-full h-full object-cover hover:scale-105 transition-transform duration-200"
                          />
                        ) : (
                          <div className="w-full h-full bg-gradient-to-br from-gray-100 to-gray-200 flex items-center justify-center">
                            <BookOpen className="h-8 w-8 text-gray-400" />
                          </div>
                        )}
                      </div>

                      {/* Book Info */}
                      <div className="space-y-2">
                        <h4 className="font-medium text-sm line-clamp-2 hover:text-blue-600 transition-colors">
                          {favorite.title}
                        </h4>
                        <p className="text-xs text-gray-600">
                          by {favorite.author}
                        </p>

                        {/* Rating */}
                        <div className="flex items-center gap-1">
                          <div className="flex items-center">
                            {renderStars(favorite.averageRating)}
                          </div>
                          <span className="text-xs text-gray-600">
                            ({favorite.totalReviews})
                          </span>
                        </div>

                        {/* Genres */}
                        <div className="flex gap-1 flex-wrap">
                          {formatGenres(favorite.genres).map((genre, index) => (
                            <Badge key={index} variant="secondary" className="text-xs">
                              {genre}
                            </Badge>
                          ))}
                          {favorite.genres.length > 2 && (
                            <Badge variant="outline" className="text-xs">
                              +{favorite.genres.length - 2}
                            </Badge>
                          )}
                        </div>

                        {/* Added to favorites date */}
                        <p className="text-xs text-gray-500">
                          Added {formatDistanceToNow(new Date(favorite.addedToFavoritesAt), { addSuffix: true })}
                        </p>
                      </div>
                    </Link>
                  </div>
                ))}
              </div>

              {/* Pagination */}
              {totalPages > 1 && (
                <div className="flex justify-center pt-4">
                  <BookPagination
                    currentPage={page}
                    totalPages={totalPages}
                    onPageChange={handlePageChange}
                  />
                </div>
              )}

              {/* Results Summary */}
              {totalFavorites > 0 && (
                <div className="text-center text-sm text-muted-foreground pt-4 border-t">
                  Showing {Math.min(page * FAVORITES_PER_PAGE, totalFavorites)} of {totalFavorites} favorite books
                </div>
              )}
            </div>
          )}
        </CardContent>
      </Card>

      {/* Remove Confirmation Dialog */}
      <AlertDialog open={!!bookToRemove} onOpenChange={() => setBookToRemove(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Remove from Favorites</AlertDialogTitle>
            <AlertDialogDescription>
              Are you sure you want to remove this book from your favorites? 
              You can always add it back later.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel onClick={handleCancelRemove}>Cancel</AlertDialogCancel>
            <AlertDialogAction
              onClick={handleConfirmRemove}
              className="bg-red-600 hover:bg-red-700"
              disabled={toggleFavoriteMutation.isPending}
            >
              {toggleFavoriteMutation.isPending ? 'Removing...' : 'Remove'}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </>
  )
}
