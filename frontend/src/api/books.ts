import { apiClient } from './client'
import { Book, BookSearchParams, PaginatedResponse } from '@/types/book'

export const booksApi = {
  // Get paginated books with optional filters
  getBooks: async (params?: BookSearchParams): Promise<PaginatedResponse<Book>> => {
    console.log('🌐 API Request: GET /books with params:', params)
    const response = await apiClient.get('/books', { params })
    console.log('📦 API Response:', {
      status: response.status,
      totalElements: response.data?.totalElements,
      totalPages: response.data?.totalPages,
      currentPage: response.data?.number,
      size: response.data?.size,
      numberOfElements: response.data?.numberOfElements,
      firstBook: response.data?.content?.[0]?.title,
      lastBook: response.data?.content?.[response.data?.content?.length - 1]?.title
    })
    return response.data
  },

  // Get a single book by ID
  getBook: async (id: string): Promise<Book> => {
    const response = await apiClient.get(`/books/${id}`)
    return response.data
  },

  // Search books by title or author using the search parameter
  searchBooks: async (query: string, params?: BookSearchParams): Promise<PaginatedResponse<Book>> => {
    const searchParams = {
      ...params,
      search: query,
    }
    const response = await apiClient.get('/books', { params: searchParams })
    return response.data
  },

  // Get books by genre using the genres parameter
  getBooksByGenre: async (genre: string, params?: BookSearchParams): Promise<PaginatedResponse<Book>> => {
    const searchParams = {
      ...params,
      genres: [genre],
    }
    const response = await apiClient.get('/books', { params: searchParams })
    return response.data
  },

  // Get top-rated books using minRating parameter
  getTopRatedBooks: async (params?: BookSearchParams): Promise<PaginatedResponse<Book>> => {
    const searchParams = {
      ...params,
      minRating: 4.0, // Books with rating 4.0 and above
    }
    const response = await apiClient.get('/books', { params: searchParams })
    return response.data
  },

  // Get recently added books (sorted by creation date - handled by backend)
  getRecentBooks: async (params?: BookSearchParams): Promise<PaginatedResponse<Book>> => {
    const response = await apiClient.get('/books', { params })
    return response.data
  },
}
