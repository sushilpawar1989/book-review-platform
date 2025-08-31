import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { recommendationsApi } from '@/api/recommendations'
import { RecommendationResponse, RecommendationFilters } from '@/types/recommendation'

export const useRecommendations = (params?: any) => {
  return useQuery({
    queryKey: ['recommendations', params],
    queryFn: () => recommendationsApi.getRecommendations(params),
    staleTime: 0, // Always fetch fresh data
    gcTime: 0, // Don't cache
    refetchOnMount: true,
    refetchOnWindowFocus: true,
  })
}

export const useRecommendationsByGenre = (genres: string[]) => {
  return useQuery({
    queryKey: ['recommendations', 'genre', genres],
    queryFn: () => recommendationsApi.getRecommendationsByGenre(genres),
    staleTime: 0, // Always fetch fresh data
    gcTime: 0, // Don't cache
    refetchOnMount: true,
    refetchOnWindowFocus: true,
    enabled: genres.length > 0,
  })
}

export const useTopRatedRecommendations = (limit: number = 10) => {
  return useQuery({
    queryKey: ['recommendations', 'topRated', limit],
    queryFn: () => recommendationsApi.getTopRatedRecommendations({ limit }),
    staleTime: 0, // Always fetch fresh data
    gcTime: 0, // Don't cache
    refetchOnMount: true,
    refetchOnWindowFocus: true,
  })
}

export const usePersonalizedRecommendations = (userId: string, filters?: RecommendationFilters) => {
  return useQuery({
    queryKey: ['recommendations', 'personalized', userId, filters],
    queryFn: () => recommendationsApi.getPersonalizedRecommendations(userId, filters),
    staleTime: 0, // Always fetch fresh data
    gcTime: 0, // Don't cache
    refetchOnMount: true,
    refetchOnWindowFocus: true,
    enabled: !!userId,
  })
}

export const useRefreshRecommendations = () => {
  const queryClient = useQueryClient()
  
  return useMutation({
    mutationFn: recommendationsApi.refreshRecommendations,
    onSuccess: () => {
      // Invalidate all recommendation queries
      queryClient.invalidateQueries({ queryKey: ['recommendations'] })
    },
  })
}
