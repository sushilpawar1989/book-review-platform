import { Heart } from 'lucide-react'
import FavoriteBooks from '@/components/profile/FavoriteBooks'

export default function FavoritesPage() {
  return (
    <div className="container mx-auto px-4 py-8 max-w-6xl">
      {/* Page Header */}
      <div className="mb-8">
        <div className="flex items-center gap-3 mb-2">
          <Heart className="h-8 w-8 text-red-600" />
          <h1 className="text-3xl font-bold text-gray-900">My Favorite Books</h1>
        </div>
        <p className="text-gray-600">
          Browse and manage your favorite books collection
        </p>
      </div>

      {/* Favorites Component */}
      <FavoriteBooks />
    </div>
  )
}
