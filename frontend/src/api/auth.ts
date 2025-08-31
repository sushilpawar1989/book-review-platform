import { apiClient } from './client'
import { AuthResponse, LoginRequest, RegisterRequest } from '@/types/auth'

export const authApi = {
  // Login user
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    const response = await apiClient.post('/auth/login', credentials)
    return response.data
  },

  // Register new user
  register: async (userData: RegisterRequest): Promise<AuthResponse> => {
    const { confirmPassword, agreeToTerms, ...registrationData } = userData
    const response = await apiClient.post('/auth/register', registrationData)
    return response.data
  },

  // Refresh access token
  refreshToken: async (refreshToken: string): Promise<AuthResponse> => {
    const response = await apiClient.post('/auth/refresh', { refreshToken })
    return response.data
  },

  // Logout user
  logout: async (): Promise<void> => {
    await apiClient.post('/auth/logout')
  },



  // Forgot password
  forgotPassword: async (email: string): Promise<void> => {
    await apiClient.post('/auth/forgot-password', { email })
  },

  // Reset password
  resetPassword: async (token: string, newPassword: string): Promise<void> => {
    await apiClient.post('/auth/reset-password', { token, newPassword })
  },
}
