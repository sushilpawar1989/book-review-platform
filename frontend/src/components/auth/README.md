# Authentication Module

This module provides a complete authentication system for the Book Review Platform frontend.

## Features

- **JWT Token Management**: Automatic token storage in localStorage
- **Protected Routes**: Redirect unauthenticated users to login
- **Admin Routes**: Restrict access to admin-only pages
- **Auto Token Refresh**: Automatic token refresh when expired
- **Form Validation**: Client-side validation with Zod schemas
- **Toast Notifications**: User feedback for auth actions

## Components

### AuthContext
Provides authentication state and methods throughout the app.

```tsx
import { useAuth } from '@/context/AuthContext'

function MyComponent() {
  const { user, isAuthenticated, login, logout, isAdmin } = useAuth()
  
  if (!isAuthenticated) {
    return <div>Please log in</div>
  }
  
  return <div>Welcome, {user?.firstName}!</div>
}
```

### ProtectedRoute
Wrapper component that protects routes requiring authentication.

```tsx
import ProtectedRoute from '@/components/auth/ProtectedRoute'

function App() {
  return (
    <Routes>
      <Route 
        path="/dashboard" 
        element={
          <ProtectedRoute>
            <Dashboard />
          </ProtectedRoute>
        } 
      />
    </Routes>
  )
}
```

### AdminRoute
Wrapper component that protects admin-only routes.

```tsx
import AdminRoute from '@/components/auth/AdminRoute'

function App() {
  return (
    <Routes>
      <Route 
        path="/admin" 
        element={
          <AdminRoute>
            <AdminDashboard />
          </AdminRoute>
        } 
      />
    </Routes>
  )
}
```

### LogoutButton
Reusable logout button component.

```tsx
import LogoutButton from '@/components/auth/LogoutButton'

function Navbar() {
  return (
    <nav>
      <LogoutButton variant="outline" showIcon={true} />
    </nav>
  )
}
```

## Usage

### 1. Wrap your app with AuthProvider

```tsx
// main.tsx
import { AuthProvider } from '@/context/AuthContext'

ReactDOM.createRoot(document.getElementById('root')!).render(
  <BrowserRouter>
    <AuthProvider>
      <App />
    </AuthProvider>
  </BrowserRouter>
)
```

### 2. Use authentication in components

```tsx
import { useAuth } from '@/context/AuthContext'

function LoginForm() {
  const { login, isLoading } = useAuth()
  
  const handleSubmit = async (data) => {
    try {
      await login(data)
      // User is now logged in and redirected
    } catch (error) {
      // Handle login error
    }
  }
}
```

### 3. Protect routes

```tsx
// App.tsx
<Routes>
  {/* Public routes */}
  <Route path="/login" element={<LoginPage />} />
  <Route path="/register" element={<RegisterPage />} />
  
  {/* Protected routes */}
  <Route path="/dashboard" element={
    <ProtectedRoute>
      <Dashboard />
    </ProtectedRoute>
  } />
  
  {/* Admin routes */}
  <Route path="/admin" element={
    <AdminRoute>
      <AdminPanel />
    </AdminRoute>
  } />
</Routes>
```

## Token Storage

- **Access Token**: Stored in `localStorage` as `authToken`
- **Refresh Token**: Stored in `localStorage` as `refreshToken`
- **User Data**: Stored in `localStorage` as `user` (JSON string)

## API Integration

The authentication module automatically:

1. **Adds JWT tokens** to all API requests via Axios interceptors
2. **Refreshes expired tokens** automatically
3. **Redirects to login** when refresh fails
4. **Clears auth state** on logout

## Environment Variables

```env
VITE_API_BASE_URL=http://localhost:8080/api/v1
```

## Security Features

- **Automatic token refresh** prevents session expiration
- **Secure token storage** in localStorage (consider httpOnly cookies for production)
- **Route protection** prevents unauthorized access
- **CSRF protection** via SameSite cookies (when using httpOnly)
- **Input validation** prevents malicious data submission
