import { useState } from 'react'
import { Loader2, User, AlertCircle } from 'lucide-react'
import { useCurrentUserProfile } from '@/hooks/useUser'
import UserInfo from '@/components/profile/UserInfo'
import UserReviews from '@/components/profile/UserReviews'
import FavoriteBooks from '@/components/profile/FavoriteBooks'
import { Card, CardContent } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs'

export default function ProfilePage() {
  const {
    data: user,
    isLoading,
    isError,
    error,
    refetch,
  } = useCurrentUserProfile()

  if (isLoading) {
    return (
      <div className="container mx-auto px-4 py-8 max-w-6xl">
        <div className="flex items-center justify-center py-12">
          <div className="text-center">
            <Loader2 className="h-8 w-8 animate-spin mx-auto mb-4" />
            <p className="text-muted-foreground">Loading your profile...</p>
          </div>
        </div>
      </div>
    )
  }

  if (isError || !user) {
    return (
      <div className="container mx-auto px-4 py-8 max-w-6xl">
        <Card className="p-8 text-center">
          <CardContent>
            <div className="text-red-500 mb-4">
              <AlertCircle className="h-12 w-12 mx-auto mb-2" />
              <h3 className="text-lg font-semibold">Error Loading Profile</h3>
            </div>
            <p className="text-muted-foreground mb-4">
              {error instanceof Error ? error.message : 'Something went wrong while loading your profile.'}
            </p>
            <Button onClick={() => refetch()} variant="outline">
              Try Again
            </Button>
          </CardContent>
        </Card>
      </div>
    )
  }

  return (
    <div className="container mx-auto px-4 py-8 max-w-6xl">
      {/* Page Header */}
      <div className="mb-8">
        <div className="flex items-center gap-3 mb-2">
          <User className="h-8 w-8 text-blue-600" />
          <h1 className="text-3xl font-bold text-gray-900">My Profile</h1>
        </div>
        <p className="text-gray-600">
          Manage your profile, reviews, and favorite books
        </p>
      </div>

      <div className="grid lg:grid-cols-3 gap-8">
        {/* Left Column - User Info */}
        <div className="lg:col-span-1">
          <UserInfo user={user} />
        </div>

        {/* Right Column - Reviews and Favorites */}
        <div className="lg:col-span-2">
          <Tabs defaultValue="reviews" className="space-y-6">
            <TabsList className="grid w-full grid-cols-2">
              <TabsTrigger value="reviews">My Reviews</TabsTrigger>
              <TabsTrigger value="favorites">Favorite Books</TabsTrigger>
            </TabsList>
            
            <TabsContent value="reviews" className="space-y-6">
              <UserReviews />
            </TabsContent>
            
            <TabsContent value="favorites" className="space-y-6">
              <FavoriteBooks />
            </TabsContent>
          </Tabs>
        </div>
      </div>
    </div>
  )
}
