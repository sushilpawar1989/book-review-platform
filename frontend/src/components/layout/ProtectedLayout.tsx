import { Outlet, Link, useNavigate } from 'react-router-dom'
import { useAuth } from '@/context/AuthContext'
import { Button } from '@/components/ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { User, LogOut, BookOpen, Heart, Star, Home } from 'lucide-react'

export default function ProtectedLayout() {
  const { user, logout, isAuthenticated } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  if (!isAuthenticated) {
    return null
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Navigation Header */}
      <header className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between h-16">
            {/* Logo and Navigation */}
            <div className="flex items-center space-x-8">
              <Link to="/app" className="flex items-center space-x-2">
                <BookOpen className="h-8 w-8 text-blue-600" />
                <span className="text-xl font-bold text-gray-900">BookReview</span>
              </Link>
              
              <nav className="hidden md:flex space-x-6">
                <Link
                  to="/app"
                  className="text-gray-600 hover:text-gray-900 px-3 py-2 text-sm font-medium flex items-center space-x-1"
                >
                  <Home className="h-4 w-4" />
                  <span>Dashboard</span>
                </Link>
                <Link
                  to="/books"
                  className="text-gray-600 hover:text-gray-900 px-3 py-2 text-sm font-medium flex items-center space-x-1"
                >
                  <BookOpen className="h-4 w-4" />
                  <span>Books</span>
                </Link>
                <Link
                  to="/app/recommendations"
                  className="text-gray-600 hover:text-gray-900 px-3 py-2 text-sm font-medium flex items-center space-x-1"
                >
                  <Star className="h-4 w-4" />
                  <span>Recommendations</span>
                </Link>
                <Link
                  to="/app/favorites"
                  className="text-gray-600 hover:text-gray-900 px-3 py-2 text-sm font-medium flex items-center space-x-1"
                >
                  <Heart className="h-4 w-4" />
                  <span>Favorites</span>
                </Link>
              </nav>
            </div>

            {/* User Menu */}
            <div className="flex items-center space-x-4">
              <span className="text-sm text-gray-700">
                Welcome, {user?.firstName}!
              </span>
              
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button variant="ghost" className="relative h-8 w-8 rounded-full">
                    <User className="h-4 w-4" />
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent className="w-56" align="end" forceMount>
                  <DropdownMenuLabel className="font-normal">
                    <div className="flex flex-col space-y-1">
                      <p className="text-sm font-medium leading-none">
                        {user?.firstName} {user?.lastName}
                      </p>
                      <p className="text-xs leading-none text-muted-foreground">
                        {user?.email}
                      </p>
                    </div>
                  </DropdownMenuLabel>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem asChild>
                    <Link to="/app/profile" className="cursor-pointer">
                      <User className="mr-2 h-4 w-4" />
                      <span>Profile</span>
                    </Link>
                  </DropdownMenuItem>
                  <DropdownMenuItem asChild>
                    <Link to="/app/favorites" className="cursor-pointer">
                      <Heart className="mr-2 h-4 w-4" />
                      <span>Favorites</span>
                    </Link>
                  </DropdownMenuItem>
                  <DropdownMenuItem asChild>
                    <Link to="/app/reviews" className="cursor-pointer">
                      <Star className="mr-2 h-4 w-4" />
                      <span>My Reviews</span>
                    </Link>
                  </DropdownMenuItem>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem onClick={handleLogout} className="cursor-pointer">
                    <LogOut className="mr-2 h-4 w-4" />
                    <span>Log out</span>
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            </div>
          </div>
        </div>
      </header>

      {/* Mobile Navigation */}
      <nav className="md:hidden bg-white border-b px-4 py-2">
        <div className="flex space-x-4 overflow-x-auto">
          <Link
            to="/app"
            className="text-gray-600 hover:text-gray-900 px-3 py-2 text-sm font-medium whitespace-nowrap flex items-center space-x-1"
          >
            <Home className="h-4 w-4" />
            <span>Dashboard</span>
          </Link>
          <Link
            to="/books"
            className="text-gray-600 hover:text-gray-900 px-3 py-2 text-sm font-medium whitespace-nowrap flex items-center space-x-1"
          >
            <BookOpen className="h-4 w-4" />
            <span>Books</span>
          </Link>
          <Link
            to="/app/recommendations"
            className="text-gray-600 hover:text-gray-900 px-3 py-2 text-sm font-medium whitespace-nowrap flex items-center space-x-1"
          >
            <Star className="h-4 w-4" />
            <span>Recommendations</span>
          </Link>
          <Link
            to="/app/favorites"
            className="text-gray-600 hover:text-gray-900 px-3 py-2 text-sm font-medium whitespace-nowrap flex items-center space-x-1"
          >
            <Heart className="h-4 w-4" />
            <span>Favorites</span>
          </Link>
        </div>
      </nav>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Outlet />
      </main>
    </div>
  )
}
