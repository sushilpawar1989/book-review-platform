import { Link } from 'react-router-dom'
import { Star, BookOpen, Calendar } from 'lucide-react'
import { Book } from '@/types/book'
import { Card, CardContent } from '@/components/ui/card'
import { Badge } from '@/components/ui/badge'

interface BookCardProps {
  book: Book
  showDescription?: boolean
  className?: string
}

export default function BookCard({ book, showDescription = false, className }: BookCardProps) {
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

  return (
    <Card className={`group hover:shadow-lg transition-all duration-200 ${className}`}>
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
          
          {/* Rating badge overlay */}
          {book.averageRating > 0 && (
            <div className="absolute top-2 right-2 bg-black/75 text-white px-2 py-1 rounded-md text-sm font-medium">
              {book.averageRating.toFixed(1)}
            </div>
          )}
        </div>

        <CardContent className="p-4">
          <div className="space-y-2">
            {/* Title */}
            <h3 className="font-semibold text-lg leading-tight line-clamp-2 group-hover:text-primary transition-colors">
              {book.title}
            </h3>

            {/* Author */}
            <p className="text-muted-foreground text-sm">
              by {book.author}
            </p>

            {/* Rating and Reviews */}
            <div className="flex items-center gap-2">
              <div className="flex items-center">
                {renderStars(book.averageRating)}
              </div>
              <span className="text-sm text-muted-foreground">
                ({book.totalReviews} {book.totalReviews === 1 ? 'review' : 'reviews'})
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

            {/* Publication Year */}
            <div className="flex items-center gap-1 text-xs text-muted-foreground">
              <Calendar className="h-3 w-3" />
              <span>{book.publishedYear}</span>
            </div>

            {/* Description (if enabled) */}
            {showDescription && book.description && (
              <p className="text-sm text-muted-foreground line-clamp-3 mt-2">
                {book.description}
              </p>
            )}
          </div>
        </CardContent>
      </Link>
    </Card>
  )
}
