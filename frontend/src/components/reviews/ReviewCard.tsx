import { useState } from 'react'
import { Star, Edit2, Trash2, Calendar } from 'lucide-react'
import { Review } from '@/types/review'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import { useAuth } from '@/context/AuthContext'
import { formatDistanceToNow } from 'date-fns'

interface ReviewCardProps {
  review: Review
  onEdit?: (review: Review) => void
  onDelete?: (reviewId: string) => void
  isEditing?: boolean
}

export default function ReviewCard({ review, onEdit, onDelete, isEditing = false }: ReviewCardProps) {
  const { user } = useAuth()
  const [isDeleting, setIsDeleting] = useState(false)
  
  const isOwner = user?.id === review.userId
  const canEdit = isOwner && !isEditing
  const canDelete = isOwner && !isEditing

  const handleDelete = async () => {
    if (!onDelete) return
    
    if (window.confirm('Are you sure you want to delete this review? This action cannot be undone.')) {
      setIsDeleting(true)
      try {
        await onDelete(review.id)
      } finally {
        setIsDeleting(false)
      }
    }
  }

  const renderStars = (rating: number) => {
    return Array.from({ length: 5 }, (_, index) => {
      const starNumber = index + 1
      const isFilled = starNumber <= rating
      
      return (
        <Star
          key={starNumber}
          className={`h-4 w-4 ${
            isFilled ? 'text-yellow-400 fill-current' : 'text-gray-300'
          }`}
        />
      )
    })
  }

  return (
    <div className="bg-white rounded-lg border p-4 hover:shadow-md transition-shadow">
      {/* Review Header */}
      <div className="flex items-start justify-between mb-3">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-purple-600 rounded-full flex items-center justify-center text-white text-sm font-semibold">
            {review.userFirstName[0]}{review.userLastName[0]}
          </div>
          <div>
            <div className="flex items-center gap-2">
              <h4 className="font-semibold text-gray-900">
                {review.userFirstName} {review.userLastName}
              </h4>
              {isOwner && (
                <Badge variant="secondary" className="text-xs">
                  You
                </Badge>
              )}
            </div>
            <div className="flex items-center gap-1 text-sm text-gray-500">
              <Calendar className="h-3 w-3" />
              {formatDistanceToNow(new Date(review.createdAt), { addSuffix: true })}
              {review.updatedAt !== review.createdAt && (
                <span className="text-xs text-gray-400 ml-1">(edited)</span>
              )}
            </div>
          </div>
        </div>

        {/* Rating */}
        <div className="flex items-center gap-1">
          {renderStars(review.rating)}
          <span className="text-sm font-medium text-gray-700 ml-1">
            {review.rating}/5
          </span>
        </div>
      </div>

      {/* Review Text */}
      <div className="mb-4">
        <p className="text-gray-700 leading-relaxed whitespace-pre-wrap">
          {review.text}
        </p>
      </div>

      {/* Action Buttons */}
      {(canEdit || canDelete) && (
        <div className="flex gap-2 pt-3 border-t">
          {canEdit && onEdit && (
            <Button
              variant="outline"
              size="sm"
              onClick={() => onEdit(review)}
              className="flex items-center gap-1"
            >
              <Edit2 className="h-3 w-3" />
              Edit
            </Button>
          )}
          
          {canDelete && onDelete && (
            <Button
              variant="outline"
              size="sm"
              onClick={handleDelete}
              disabled={isDeleting}
              className="flex items-center gap-1 text-red-600 hover:text-red-700 hover:border-red-300"
            >
              <Trash2 className="h-3 w-3" />
              {isDeleting ? 'Deleting...' : 'Delete'}
            </Button>
          )}
        </div>
      )}
    </div>
  )
}
