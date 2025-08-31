import { ChevronLeft, ChevronRight, MoreHorizontal } from 'lucide-react'
import { Button } from '@/components/ui/button'
import { cn } from '@/utils/cn'

interface BookPaginationProps {
  currentPage: number
  totalPages: number
  onPageChange: (page: number) => void
  className?: string
}

export default function BookPagination({
  currentPage,
  totalPages,
  onPageChange,
  className = '',
}: BookPaginationProps) {
  if (totalPages <= 1) return null

  const handlePageClick = (page: number) => {
    console.log('🖱️ Pagination click:', { page, currentPage, totalPages })
    if (page >= 1 && page <= totalPages && page !== currentPage) {
      console.log('✅ Calling onPageChange with:', page)
      onPageChange(page)
    } else {
      console.log('❌ Page click ignored:', { reason: page < 1 ? 'page < 1' : page > totalPages ? 'page > totalPages' : 'same page' })
    }
  }

  const generatePageNumbers = () => {
    const pages: (number | 'ellipsis')[] = []
    const showEllipsis = totalPages > 7

    if (!showEllipsis) {
      // Show all pages if total pages <= 7
      for (let i = 1; i <= totalPages; i++) {
        pages.push(i)
      }
    } else {
      // Always show first page
      pages.push(1)

      if (currentPage <= 3) {
        // Current page is near the beginning
        for (let i = 2; i <= Math.min(5, totalPages - 1); i++) {
          pages.push(i)
        }
        if (totalPages > 5) {
          pages.push('ellipsis')
        }
      } else if (currentPage >= totalPages - 2) {
        // Current page is near the end
        if (totalPages > 5) {
          pages.push('ellipsis')
        }
        for (let i = Math.max(totalPages - 4, 2); i <= totalPages - 1; i++) {
          pages.push(i)
        }
      } else {
        // Current page is in the middle
        pages.push('ellipsis')
        for (let i = currentPage - 1; i <= currentPage + 1; i++) {
          pages.push(i)
        }
        pages.push('ellipsis')
      }

      // Always show last page (if not already included)
      if (totalPages > 1) {
        pages.push(totalPages)
      }
    }

    return pages
  }

  const pageNumbers = generatePageNumbers()

  return (
    <nav
      role="navigation"
      aria-label="pagination"
      className={cn("mx-auto flex w-full justify-center", className)}
    >
      <div className="flex flex-row items-center gap-1">
        {/* Previous button */}
        <Button
          variant="outline"
          size="sm"
          onClick={() => handlePageClick(currentPage - 1)}
          disabled={currentPage <= 1}
          className="gap-1 pl-2.5"
        >
          <ChevronLeft className="h-4 w-4" />
          <span>Previous</span>
        </Button>

        {/* Page numbers */}
        {pageNumbers.map((page, index) => (
          <div key={index}>
            {page === 'ellipsis' ? (
              <span className="flex h-9 w-9 items-center justify-center">
                <MoreHorizontal className="h-4 w-4" />
                <span className="sr-only">More pages</span>
              </span>
            ) : (
              <Button
                variant={page === currentPage ? "default" : "ghost"}
                size="sm"
                onClick={() => handlePageClick(page)}
                className="h-9 w-9"
              >
                {page}
              </Button>
            )}
          </div>
        ))}

        {/* Next button */}
        <Button
          variant="outline"
          size="sm"
          onClick={() => handlePageClick(currentPage + 1)}
          disabled={currentPage >= totalPages}
          className="gap-1 pr-2.5"
        >
          <span>Next</span>
          <ChevronRight className="h-4 w-4" />
        </Button>
      </div>
    </nav>
  )
}
