export interface Book {
  id: string
  title: string
  author: string
  description: string
  coverImageUrl?: string
  genres: string[]
  publishedYear: number
  averageRating: number
  totalReviews: number
  createdAt: string
  updatedAt: string
}

export interface BookFilters {
  genres?: string[]
  author?: string
  title?: string
  search?: string
  publishedYear?: number
  minPublishedYear?: number
  maxPublishedYear?: number
  minRating?: number
  maxRating?: number
  minReviews?: number
}

export interface PaginationParams {
  page?: number
  size?: number
  sortBy?: string
  sortOrder?: 'asc' | 'desc'
}

export interface PaginatedResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
  numberOfElements: number
}

export interface BookSearchParams extends BookFilters, PaginationParams {}

export const GENRES = [
  'FICTION',
  'NON_FICTION',
  'MYSTERY',
  'THRILLER',
  'ROMANCE',
  'SCIENCE_FICTION',
  'FANTASY',
  'HORROR',
  'BIOGRAPHY',
  'HISTORY',
  'SELF_HELP',
  'POETRY',
  'DRAMA',
  'COMEDY',
  'ACTION',
  'ADVENTURE',
  'CRIME',
  'DOCUMENTARY',
  'EDUCATIONAL',
  'OTHER'
] as const

export type Genre = typeof GENRES[number]
