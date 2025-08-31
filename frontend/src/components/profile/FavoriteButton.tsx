import { Heart, Loader2 } from 'lucide-react'
import { useAuth } from '@/context/AuthContext'
import { useIsFavorite, useToggleFavorite } from '@/hooks/useUser'
import { Button } from '@/components/ui/button'
import { cn } from '@/utils/cn'
import { toast } from '@/hooks/use-toast'

interface FavoriteButtonProps {
  bookId: string
  variant?: 'default' | 'outline' | 'ghost'
  size?: 'default' | 'sm' | 'lg'
  showText?: boolean
  className?: string
  disabled?: boolean
}

export default function FavoriteButton({
  bookId,
  variant = 'outline',
  size = 'default',
  showText = true,
  className,
  disabled = false,
}: FavoriteButtonProps) {
  const { isAuthenticated } = useAuth()
  
  const {
    data: favoriteStatus,
    isLoading: isCheckingFavorite,
  } = useIsFavorite(bookId)

  const toggleFavoriteMutation = useToggleFavorite()

  const isFavorite = favoriteStatus?.isFavorite || false
  const isLoading = isCheckingFavorite || toggleFavoriteMutation.isPending

  // Debug logging
  console.log('🔍 FavoriteButton state:', {
    bookId,
    favoriteStatus,
    isFavorite,
    isCheckingFavorite,
    isLoading
  })

  const handleToggle = () => {
    console.log('🔄 FavoriteButton handleToggle:', { 
      bookId, 
      isAuthenticated, 
      disabled, 
      isFavorite,
      isLoading,
      token: localStorage.getItem('authToken') ? 'Present' : 'Missing'
    })
    
    if (!isAuthenticated) {
      console.log('❌ User not authenticated')
      toast({
        title: 'Authentication Required',
        description: 'Please log in to add books to favorites',
        variant: 'destructive',
      })
      return
    }
    
    if (disabled) {
      console.log('❌ Button disabled')
      return
    }
    
    console.log('🚀 Toggling favorite for book:', bookId)
    toggleFavoriteMutation.mutate({ bookId }, {
      onSuccess: (data) => {
        console.log('✅ Toggle favorite success:', data)
        toast({
          title: data.isFavorite ? 'Added to Favorites' : 'Removed from Favorites',
          description: data.message,
        })
      },
      onError: (error) => {
        console.error('❌ Toggle favorite error:', error)
        toast({
          title: 'Error',
          description: error.message || 'Failed to update favorites',
          variant: 'destructive',
        })
      }
    })
  }

  // Don't render if user is not authenticated
  if (!isAuthenticated) {
    return null
  }

  return (
    <Button
      variant={isFavorite ? 'default' : variant}
      size={size}
      onClick={handleToggle}
      disabled={disabled || isLoading}
      className={cn(
        isFavorite && 'bg-red-500 hover:bg-red-600 text-white',
        className
      )}
    >
      {isLoading ? (
        <Loader2 className="h-4 w-4 animate-spin" />
      ) : (
        <Heart
          className={cn(
            'h-4 w-4',
            isFavorite && 'fill-current',
            showText && 'mr-2'
          )}
        />
      )}
      {showText && (
        <span>
          {isLoading 
            ? 'Loading...' 
            : isFavorite 
              ? 'Remove from Favorites' 
              : 'Add to Favorites'
          }
        </span>
      )}
    </Button>
  )
}
