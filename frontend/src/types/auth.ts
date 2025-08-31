export interface User {
  id: string
  email: string
  firstName: string
  lastName: string
  role: 'USER' | 'ADMIN'
  bio?: string
  preferredGenres: string[]
  createdAt: string
}

export interface AuthState {
  user: User | null
  token: string | null
  refreshToken: string | null
  isAuthenticated: boolean
  isLoading: boolean
}

export interface LoginRequest {
  email: string
  password: string
  rememberMe?: boolean
}

export interface RegisterRequest {
  email: string
  password: string
  firstName: string
  lastName: string
  confirmPassword: string
  agreeToTerms: boolean
}

export interface AuthResponse {
  user: User
  accessToken: string
  refreshToken: string
  tokenType: string
  expiresIn: number
}

export interface AuthContextType {
  user: User | null
  token: string | null
  isAuthenticated: boolean
  isLoading: boolean
  login: (credentials: LoginRequest) => Promise<void>
  register: (userData: RegisterRequest) => Promise<void>
  logout: () => void
  refreshToken: () => Promise<void>
  isAdmin: boolean
}
