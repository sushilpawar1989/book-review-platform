import { Link } from 'react-router-dom'
import { User, BookOpen, Heart, MessageSquare, TrendingUp } from 'lucide-react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'

export default function DashboardPage() {
  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-3xl font-bold text-gray-900 mb-2">
          Welcome to Your Dashboard
        </h1>
        <p className="text-gray-600">
          Manage your reading journey and discover your next favorite book.
        </p>
      </div>

      <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Profile</CardTitle>
            <User className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">Manage</div>
            <p className="text-xs text-muted-foreground">
              Update your profile and preferences
            </p>
            <Button asChild className="w-full mt-3" variant="outline">
              <Link to="/app/profile">View Profile</Link>
            </Button>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Books</CardTitle>
            <BookOpen className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">Discover</div>
            <p className="text-xs text-muted-foreground">
              Browse our book collection
            </p>
            <Button asChild className="w-full mt-3" variant="outline">
              <Link to="/books">Browse Books</Link>
            </Button>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Favorites</CardTitle>
            <Heart className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">Coming</div>
            <p className="text-xs text-muted-foreground">
              Your favorite books
            </p>
            <Button className="w-full mt-3" variant="outline" disabled>
              View Favorites
            </Button>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
            <CardTitle className="text-sm font-medium">Recommendations</CardTitle>
            <TrendingUp className="h-4 w-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">Discover</div>
            <p className="text-xs text-muted-foreground">
              Personalized book suggestions
            </p>
            <Button asChild className="w-full mt-3" variant="outline">
              <Link to="/app/recommendations">View Recommendations</Link>
            </Button>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
