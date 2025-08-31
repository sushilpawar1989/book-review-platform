import { useState, useEffect } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import * as z from 'zod'
import { Star, Loader2, Edit2, Plus } from 'lucide-react'
import { Review, CreateReviewRequest, UpdateReviewRequest } from '@/types/review'
import { Button } from '@/components/ui/button'
import { Textarea } from '@/components/ui/textarea'
import { Label } from '@/components/ui/label'
import { toast } from '@/hooks/use-toast'

const reviewSchema = z.object({
  rating: z.number().min(1, 'Rating is required').max(5, 'Rating must be between 1 and 5'),
  text: z.string().min(10, 'Review must be at least 10 characters').max(2000, 'Review must be less than 2000 characters'),
})

type ReviewFormData = z.infer<typeof reviewSchema>

interface ReviewFormProps {
  bookId: string
  bookTitle: string
  existingReview?: Review | null
  onSubmit: (data: CreateReviewRequest | UpdateReviewRequest) => Promise<void>
  onCancel: () => void
  isSubmitting?: boolean
}

export default function ReviewForm({ 
  bookId, 
  bookTitle, 
  existingReview, 
  onSubmit, 
  onCancel, 
  isSubmitting = false 
}: ReviewFormProps) {
  const [hoveredRating, setHoveredRating] = useState<number | null>(null)
  
  const {
    register,
    handleSubmit,
    formState: { errors },
    setValue,
    watch,
    reset,
  } = useForm<ReviewFormData>({
    resolver: zodResolver(reviewSchema),
    defaultValues: {
      rating: existingReview?.rating || 0,
      text: existingReview?.text || '',
    },
  })

  const currentRating = watch('rating')

  // Update form when existingReview changes
  useEffect(() => {
    if (existingReview) {
      reset({
        rating: existingReview.rating,
        text: existingReview.text,
      })
    }
  }, [existingReview, reset])

  const handleRatingClick = (rating: number) => {
    setValue('rating', rating)
  }

  const handleFormSubmit = async (data: ReviewFormData) => {
    try {
      if (existingReview) {
        // Update existing review
        await onSubmit({
          rating: data.rating,
          text: data.text,
        } as UpdateReviewRequest)
      } else {
        // Create new review
        await onSubmit({
          bookId,
          rating: data.rating,
          text: data.text,
        } as CreateReviewRequest)
      }
      
      toast({
        title: existingReview ? 'Review Updated' : 'Review Created',
        description: existingReview 
          ? 'Your review has been successfully updated.'
          : 'Your review has been successfully posted.',
      })
    } catch (error: any) {
      toast({
        title: 'Error',
        description: error.response?.data?.message || 'Something went wrong. Please try again.',
        variant: 'destructive',
      })
    }
  }

  const renderStars = () => {
    return Array.from({ length: 5 }, (_, index) => {
      const starNumber = index + 1
      const isFilled = starNumber <= (hoveredRating || currentRating)
      const isHovered = starNumber <= (hoveredRating || 0)
      
      return (
        <button
          key={starNumber}
          type="button"
          className={`p-1 transition-colors ${
            isFilled ? 'text-yellow-400' : 'text-gray-300'
          } hover:text-yellow-400`}
          onMouseEnter={() => setHoveredRating(starNumber)}
          onMouseLeave={() => setHoveredRating(null)}
          onClick={() => handleRatingClick(starNumber)}
        >
          <Star className={`h-6 w-6 ${isHovered ? 'scale-110' : ''} transition-transform`} fill={isFilled ? 'currentColor' : 'none'} />
        </button>
      )
    })
  }

  return (
    <div className="bg-white rounded-lg border p-6">
      <div className="mb-4">
        <h3 className="text-lg font-semibold text-gray-900 mb-2">
          {existingReview ? (
            <>
              <Edit2 className="inline h-4 w-4 mr-2" />
              Edit Your Review
            </>
          ) : (
            <>
              <Plus className="inline h-4 w-4 mr-2" />
              Write a Review
            </>
          )}
        </h3>
        <p className="text-sm text-gray-600">
          {existingReview ? 'Update your review for' : 'Share your thoughts on'} <span className="font-medium">{bookTitle}</span>
        </p>
      </div>

      <form onSubmit={handleSubmit(handleFormSubmit)} className="space-y-4">
        {/* Rating */}
        <div className="space-y-2">
          <Label htmlFor="rating">Rating *</Label>
          <div className="flex items-center gap-2">
            {renderStars()}
            <span className="ml-2 text-sm text-gray-600">
              {currentRating > 0 ? `${currentRating} out of 5` : 'Click to rate'}
            </span>
          </div>
          {errors.rating && (
            <p className="text-sm text-red-500">{errors.rating.message}</p>
          )}
        </div>

        {/* Review Text */}
        <div className="space-y-2">
          <Label htmlFor="text">Review *</Label>
          <Textarea
            id="text"
            placeholder="Share your thoughts about this book... (minimum 10 characters)"
            rows={4}
            {...register('text')}
            className={errors.text ? 'border-red-500' : ''}
          />
          {errors.text && (
            <p className="text-sm text-red-500">{errors.text.message}</p>
          )}
          <p className="text-xs text-gray-500">
            {watch('text')?.length || 0}/2000 characters
          </p>
        </div>

        {/* Form Actions */}
        <div className="flex gap-3 pt-2">
          <Button 
            type="submit" 
            disabled={isSubmitting || currentRating === 0}
            className="flex-1"
          >
            {isSubmitting ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                {existingReview ? 'Updating...' : 'Posting...'}
              </>
            ) : (
              existingReview ? 'Update Review' : 'Post Review'
            )}
          </Button>
          <Button 
            type="button" 
            variant="outline" 
            onClick={onCancel}
            disabled={isSubmitting}
          >
            Cancel
          </Button>
        </div>
      </form>
    </div>
  )
}
