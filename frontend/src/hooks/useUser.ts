import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { userApi } from '@/api/user'
import { UserProfile, UpdateProfileRequest, FavoriteToggleRequest } from '@/types/user'
import { PaginationParams } from '@/types/book'

export const useUserProfile = (userId: string) => {
  return useQuery({
    queryKey: ['user', 'profile', userId],
    queryFn: () => userApi.getProfile(),
    staleTime: 0, // Always fetch fresh data
    gcTime: 0, // Don't cache
    refetchOnMount: true,
    refetchOnWindowFocus: true,
    enabled: !!userId,
  })
}

export const useCurrentUserProfile = () => {
  return useQuery({
    queryKey: ['user', 'profile', 'current'],
    queryFn: () => userApi.getCurrentUserProfile(),
    staleTime: 0, // Always fetch fresh data
    gcTime: 0, // Don't cache
    refetchOnMount: true,
    refetchOnWindowFocus: true,
  })
}

export const useUserStats = (userId: string) => {
  return useQuery({
    queryKey: ['user', 'stats', userId],
    queryFn: () => userApi.getUserStats(),
    staleTime: 0, // Always fetch fresh data
    gcTime: 0, // Don't cache
    refetchOnMount: true,
    refetchOnWindowFocus: true,
    enabled: !!userId,
  })
}

export const useUserFavoriteBooks = (params?: PaginationParams) => {
  return useQuery({
    queryKey: ['user', 'favorites', params],
    queryFn: () => userApi.getFavoriteBooks(params),
    staleTime: 0, // Always fetch fresh data
    gcTime: 0, // Don't cache
    refetchOnMount: true,
    refetchOnWindowFocus: true,
  })
}

export const useIsFavorite = (bookId: string) => {
  return useQuery({
    queryKey: ['user', 'favorite', bookId],
    queryFn: () => userApi.isFavorite(bookId),
    staleTime: 0, // Always fetch fresh data
    gcTime: 0, // Don't cache
    refetchOnMount: true,
    refetchOnWindowFocus: true,
    enabled: !!bookId,
  })
}

export const useToggleFavorite = () => {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: (request: FavoriteToggleRequest) => userApi.toggleFavorite(request),
    onSuccess: (data, variables) => {
      console.log('🔄 Toggle favorite success, refetching data:', { data, variables })
      
      // Force refetch all related queries to get fresh data
      queryClient.refetchQueries({ queryKey: ['user', 'favorite', variables.bookId] })
      queryClient.refetchQueries({ queryKey: ['user', 'favorites'] })
      queryClient.refetchQueries({ queryKey: ['user', 'stats'] })
      queryClient.refetchQueries({ queryKey: ['user', 'profile'] })
    },
  })
}

export const useUpdateProfile = () => {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: (data: UpdateProfileRequest) => userApi.updateProfile(data),
    onSuccess: (data) => {
      // Invalidate user profile queries
      queryClient.invalidateQueries({ queryKey: ['user', 'profile'] })
      queryClient.invalidateQueries({ queryKey: ['user', 'stats'] })
    },
  })
}

export const useChangePassword = () => {
  return useMutation({
    mutationFn: (data: { currentPassword: string; newPassword: string }) => 
      userApi.changePassword(data.currentPassword, data.newPassword),
  })
}

export const useDeleteAccount = () => {
  return useMutation({
    mutationFn: (data: { password: string }) => userApi.deleteAccount(data.password),
  })
}
