import { Link } from 'react-router-dom'
import { BookOpen, Star, Users, TrendingUp } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'

export default function HomePage() {
  return (
    <div className="min-h-screen bg-gradient-to-b from-blue-50 to-white">
      {/* Hero Section */}
      <section className="container mx-auto px-4 py-20 text-center">
        <div className="max-w-3xl mx-auto">
          <h1 className="text-5xl font-bold text-gray-900 mb-6">
            Discover Your Next
            <span className="text-blue-600"> Great Read</span>
          </h1>
          <p className="text-xl text-gray-600 mb-8">
            Join our community of book lovers. Browse, review, and get personalized recommendations for your favorite books.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center">
            <Button asChild size="lg">
              <Link to="/books">
                <BookOpen className="mr-2 h-5 w-5" />
                Explore Books
              </Link>
            </Button>
            <Button asChild variant="outline" size="lg">
              <Link to="/register">
                Join Community
              </Link>
            </Button>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="container mx-auto px-4 py-16">
        <div className="text-center mb-12">
          <h2 className="text-3xl font-bold text-gray-900 mb-4">
            Why Choose Our Platform?
          </h2>
          <p className="text-lg text-gray-600 max-w-2xl mx-auto">
            Everything you need to discover, track, and share your reading journey
          </p>
        </div>

        <div className="grid md:grid-cols-3 gap-8">
          <Card className="text-center">
            <CardHeader>
              <div className="mx-auto w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center mb-4">
                <BookOpen className="h-6 w-6 text-blue-600" />
              </div>
              <CardTitle>Extensive Library</CardTitle>
              <CardDescription>
                Browse thousands of books across all genres
              </CardDescription>
            </CardHeader>
          </Card>

          <Card className="text-center">
            <CardHeader>
              <div className="mx-auto w-12 h-12 bg-yellow-100 rounded-lg flex items-center justify-center mb-4">
                <Star className="h-6 w-6 text-yellow-600" />
              </div>
              <CardTitle>Smart Reviews</CardTitle>
              <CardDescription>
                Read detailed reviews and ratings from fellow readers
              </CardDescription>
            </CardHeader>
          </Card>

          <Card className="text-center">
            <CardHeader>
              <div className="mx-auto w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center mb-4">
                <TrendingUp className="h-6 w-6 text-green-600" />
              </div>
              <CardTitle>Personalized Recommendations</CardTitle>
              <CardDescription>
                Get book suggestions tailored to your preferences
              </CardDescription>
            </CardHeader>
          </Card>
        </div>
      </section>

      {/* CTA Section */}
      <section className="bg-blue-600 text-white py-16">
        <div className="container mx-auto px-4 text-center">
          <h2 className="text-3xl font-bold mb-4">
            Ready to Start Your Reading Journey?
          </h2>
          <p className="text-xl mb-8 opacity-90">
            Join thousands of book lovers and discover your next favorite book today.
          </p>
          <Button asChild size="lg" variant="secondary">
            <Link to="/register">
              Get Started Free
            </Link>
          </Button>
        </div>
      </section>
    </div>
  )
}
