import { Routes, Route } from 'react-router-dom'
import { Toaster } from '@/components/ui/toaster'

// Layouts
import PublicLayout from '@/components/layout/PublicLayout'
import ProtectedLayout from '@/components/layout/ProtectedLayout'
import AdminLayout from '@/components/layout/AdminLayout'

// Route Guards
import ProtectedRoute from '@/components/auth/ProtectedRoute'
import AdminRoute from '@/components/auth/AdminRoute'

// Public Pages
import HomePage from '@/pages/HomePage'
import LoginPage from '@/pages/auth/LoginPage'
import RegisterPage from '@/pages/auth/RegisterPage'
import BooksPage from '@/pages/books/BooksPage'
import BookDetailsPage from '@/pages/books/BookDetailsPage'

// Protected Pages
import DashboardPage from '@/pages/DashboardPage'
import ProfilePage from '@/pages/profile/ProfilePage'
import FavoritesPage from '@/pages/profile/FavoritesPage'
import ReviewsPage from '@/pages/reviews/ReviewsPage'
import RecommendationsPage from '@/pages/recommendations/RecommendationsPage'

// Admin Pages
import AdminDashboard from '@/pages/admin/AdminDashboard'
import BookManagement from '@/pages/admin/BookManagement'

function App() {
  return (
    <>
      <Routes>
        {/* Public Routes */}
        <Route path="/" element={<PublicLayout />}>
          <Route index element={<HomePage />} />
          <Route path="login" element={<LoginPage />} />
          <Route path="register" element={<RegisterPage />} />
          <Route path="books" element={<BooksPage />} />
          <Route path="books/:id" element={<BookDetailsPage />} />
        </Route>

        {/* Protected Routes */}
        <Route
          path="/app"
          element={
            <ProtectedRoute>
              <ProtectedLayout />
            </ProtectedRoute>
          }
        >
          <Route index element={<DashboardPage />} />
          <Route path="profile" element={<ProfilePage />} />
          <Route path="favorites" element={<FavoritesPage />} />
          <Route path="reviews" element={<ReviewsPage />} />
          <Route path="recommendations" element={<RecommendationsPage />} />
        </Route>

        {/* Admin Routes */}
        <Route
          path="/admin"
          element={
            <AdminRoute>
              <AdminLayout />
            </AdminRoute>
          }
        >
          <Route index element={<AdminDashboard />} />
          <Route path="books" element={<BookManagement />} />
        </Route>
      </Routes>
      
      <Toaster />
    </>
  )
}

export default App
