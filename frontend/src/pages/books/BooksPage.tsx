import { useState } from 'react'
import { Link } from 'react-router-dom'

export default function BooksPage() {
  const [books, setBooks] = useState<any[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [currentPage, setCurrentPage] = useState(1)
  const [searchQuery, setSearchQuery] = useState('')
  
  console.log('🔍 BooksPage render:', { currentPage, booksCount: books.length, isLoading, searchQuery })
  
  const fetchBooks = async (page: number, search: string = '') => {
    console.log('🌐 Fetching books for page:', page, 'search:', search)
    setIsLoading(true)
    
    try {
      const backendPage = page - 1
      let apiUrl = `http://localhost:8080/api/v1/books?page=${backendPage}&size=12&sortBy=title&sortOrder=asc`
      
      if (search.trim()) {
        apiUrl += `&search=${encodeURIComponent(search.trim())}`
      }
      
      console.log('🌐 API call to:', apiUrl)
      
      const response = await fetch(apiUrl)
      const result = await response.json()
      
      console.log('📦 Books loaded:', result.content?.length, 'books for page', page)
      
      setBooks(result.content || [])
      
    } catch (err) {
      console.error('❌ Error fetching books:', err)
    } finally {
      setIsLoading(false)
    }
  }
  
  const handlePageChange = (newPage: number) => {
    console.log('📄 Changing to page:', newPage)
    setCurrentPage(newPage)
    fetchBooks(newPage, searchQuery)
  }
  
  const handleSearch = () => {
    console.log('🔍 Searching for:', searchQuery)
    setCurrentPage(1) // Reset to first page when searching
    fetchBooks(1, searchQuery)
  }
  
  const handleClearSearch = () => {
    console.log('🧹 Clearing search')
    setSearchQuery('')
    setCurrentPage(1)
    fetchBooks(1, '')
  }
  
  const handleLoadInitial = () => {
    console.log('🚀 Loading initial data')
    fetchBooks(1, '')
  }
  
  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold text-gray-900 mb-8">Books Page</h1>
      
      {/* Search Section */}
      <div className="mb-8">
        <div className="flex flex-col sm:flex-row gap-4 items-start sm:items-center mb-4">
          <div className="flex-1 max-w-md">
            <input
              type="text"
              placeholder="Search by title or author..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
              className="w-full px-4 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
          
          <div className="flex gap-2">
            <button 
              onClick={handleSearch}
              className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
            >
              Search
            </button>
            
            {searchQuery && (
              <button 
                onClick={handleClearSearch}
                className="bg-gray-500 text-white px-4 py-2 rounded hover:bg-gray-600"
              >
                Clear
              </button>
            )}
          </div>
        </div>
        
        {/* Control Buttons */}
        <div className="flex flex-wrap gap-2">
          <button 
            onClick={handleLoadInitial}
            className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600"
          >
            Load Books
          </button>
          
          <button 
            onClick={() => handlePageChange(1)}
            className={`px-4 py-2 rounded hover:opacity-80 ${currentPage === 1 ? 'bg-blue-500 text-white' : 'bg-gray-200'}`}
          >
            Page 1
          </button>
          
          <button 
            onClick={() => handlePageChange(2)}
            className={`px-4 py-2 rounded hover:opacity-80 ${currentPage === 2 ? 'bg-blue-500 text-white' : 'bg-gray-200'}`}
          >
            Page 2
          </button>
          
          <button 
            onClick={() => handlePageChange(3)}
            className={`px-4 py-2 rounded hover:opacity-80 ${currentPage === 3 ? 'bg-blue-500 text-white' : 'bg-gray-200'}`}
          >
            Page 3
          </button>
        </div>
      </div>
      
      {/* Loading State */}
      {isLoading && (
        <div className="text-center py-12">
          <p className="text-lg">
            {searchQuery ? `Searching for "${searchQuery}"...` : `Loading page ${currentPage}...`}
          </p>
        </div>
      )}
      
      {/* Books Content */}
      {!isLoading && books.length > 0 && (
        <div>
          <h2 className="text-xl font-semibold mb-4">
            {searchQuery ? `Search Results for "${searchQuery}"` : `Books on Page ${currentPage}`}
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {books.map((book: any) => (
              <Link 
                key={book.id} 
                to={`/books/${book.id}`}
                className="border p-4 rounded hover:shadow-md transition-shadow block hover:bg-gray-50"
              >
                <h3 className="font-semibold text-lg text-blue-600 hover:text-blue-800">{book.title}</h3>
                <p className="text-gray-600">by {book.author}</p>
                <p className="text-sm text-gray-500 mt-2">
                  Rating: {book.averageRating ? `${book.averageRating}/5` : 'N/A'}
                </p>
                {book.description && (
                  <p className="text-sm text-gray-600 mt-2 line-clamp-3">
                    {book.description}
                  </p>
                )}
                <p className="text-xs text-blue-500 mt-3 font-medium">Click to view details →</p>
              </Link>
            ))}
          </div>
        </div>
      )}
      
      {/* No Results State */}
      {!isLoading && books.length === 0 && (
        <div className="text-center py-12">
          {searchQuery ? (
            <div>
              <p className="text-lg text-gray-600 mb-2">
                No books found for "{searchQuery}"
              </p>
              <p className="text-sm text-gray-500 mb-4">
                Try a different search term or clear the search
              </p>
              <button 
                onClick={handleClearSearch}
                className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
              >
                Clear Search
              </button>
            </div>
          ) : (
            <p className="text-gray-600">No books loaded. Click "Load Books" to get started.</p>
          )}
        </div>
      )}
    </div>
  )
}
