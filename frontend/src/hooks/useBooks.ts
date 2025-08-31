import { useQuery } from '@tanstack/react-query'
import { booksApi } from '@/api/books'
import { Book, PaginatedResponse, BookSearchParams } from '@/types/book'

export const useBooks = (params: BookSearchParams) => {
  return useQuery({
    queryKey: ['books', params],
    queryFn: () => booksApi.getBooks(params),
    staleTime: 0, // Always fetch fresh data
    gcTime: 0, // Don't cache
    refetchOnMount: true,
    refetchOnWindowFocus: true,
  })
}

export const useBook = (id: string) => {
  return useQuery({
    queryKey: ['book', id],
    queryFn: () => booksApi.getBook(id),
    staleTime: 0, // Always fetch fresh data
    gcTime: 0, // Don't cache
    refetchOnMount: true,
    refetchOnWindowFocus: true,
  })
}

export const useBookSearch = (searchParams: BookSearchParams) => {
  return useQuery({
    queryKey: ['bookSearch', searchParams],
    queryFn: () => booksApi.getBooks(searchParams),
    staleTime: 0, // Always fetch fresh data
    gcTime: 0, // Don't cache
    refetchOnMount: true,
    refetchOnWindowFocus: true,
  })
}
