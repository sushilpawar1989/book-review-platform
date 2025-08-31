import React, { createContext, useContext, useEffect, useState } from 'react'
import { User, AuthState, AuthContextType, LoginRequest, RegisterRequest } from '@/types/auth'
import { authApi } from '@/api/auth'
import { toast } from '@/hooks/use-toast'

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export const useAuth = () => {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}

interface AuthProviderProps {
  children: React.ReactNode
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [state, setState] = useState<AuthState>({
    user: null,
    token: null,
    refreshToken: null,
    isAuthenticated: false,
    isLoading: true,
  })

  // Initialize auth state from localStorage
  useEffect(() => {
    const initializeAuth = async () => {
      try {
        const token = localStorage.getItem('authToken')
        const refreshToken = localStorage.getItem('refreshToken')
        const userStr = localStorage.getItem('user')

        if (token && userStr) {
          const user: User = JSON.parse(userStr)
          setState({
            user,
            token,
            refreshToken,
            isAuthenticated: true,
            isLoading: false,
          })
          
          // Token will be validated automatically on first authenticated request
          // No need for explicit verification endpoint
        } else {
          setState(prev => ({ ...prev, isLoading: false }))
        }
      } catch (error) {
        console.error('Error initializing auth:', error)
        clearAuthState()
      }
    }

    initializeAuth()
  }, [])

  const setAuthState = (user: User, token: string, refreshToken: string) => {
    localStorage.setItem('authToken', token)
    localStorage.setItem('refreshToken', refreshToken)
    localStorage.setItem('user', JSON.stringify(user))
    
    setState({
      user,
      token,
      refreshToken,
      isAuthenticated: true,
      isLoading: false,
    })
  }

  const clearAuthState = () => {
    localStorage.removeItem('authToken')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('user')
    
    setState({
      user: null,
      token: null,
      refreshToken: null,
      isAuthenticated: false,
      isLoading: false,
    })
  }

  const login = async (credentials: LoginRequest) => {
    try {
      setState(prev => ({ ...prev, isLoading: true }))
      
      const response = await authApi.login(credentials)
      const { user, accessToken, refreshToken } = response
      
      setAuthState(user, accessToken, refreshToken)
      
      toast({
        title: 'Login Successful',
        description: `Welcome back, ${user.firstName}!`,
      })
    } catch (error: any) {
      const message = error.response?.data?.message || 'Login failed. Please try again.'
      toast({
        title: 'Login Failed',
        description: message,
        variant: 'destructive',
      })
      setState(prev => ({ ...prev, isLoading: false }))
      throw error
    }
  }

  const register = async (userData: RegisterRequest) => {
    try {
      setState(prev => ({ ...prev, isLoading: true }))
      
      const response = await authApi.register(userData)
      const { user, accessToken, refreshToken } = response
      
      setAuthState(user, accessToken, refreshToken)
      
      toast({
        title: 'Registration Successful',
        description: `Welcome to Book Review Platform, ${user.firstName}!`,
      })
    } catch (error: any) {
      const message = error.response?.data?.message || 'Registration failed. Please try again.'
      toast({
        title: 'Registration Failed',
        description: message,
        variant: 'destructive',
      })
      setState(prev => ({ ...prev, isLoading: false }))
      throw error
    }
  }

  const logout = () => {
    try {
      // Call logout API if needed
      authApi.logout().catch(console.error)
    } catch (error) {
      console.error('Logout API error:', error)
    } finally {
      clearAuthState()
      toast({
        title: 'Logged Out',
        description: 'You have been successfully logged out.',
      })
    }
  }

  const refreshTokenFunc = async () => {
    try {
      const currentRefreshToken = localStorage.getItem('refreshToken')
      if (!currentRefreshToken) {
        throw new Error('No refresh token available')
      }

      const response = await authApi.refreshToken(currentRefreshToken)
      const { user, accessToken, refreshToken: newRefreshToken } = response
      
      setAuthState(user, accessToken, newRefreshToken)
    } catch (error) {
      console.error('Token refresh failed:', error)
      clearAuthState()
      throw error
    }
  }

  const value: AuthContextType = {
    user: state.user,
    token: state.token,
    isAuthenticated: state.isAuthenticated,
    isLoading: state.isLoading,
    login,
    register,
    logout,
    refreshToken: refreshTokenFunc,
    isAdmin: state.user?.role === 'ADMIN',
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
