import { LogOut } from 'lucide-react'
import { useAuth } from '@/context/AuthContext'
import { Button } from '@/components/ui/button'

interface LogoutButtonProps {
  variant?: 'default' | 'outline' | 'ghost'
  size?: 'default' | 'sm' | 'lg'
  showIcon?: boolean
  className?: string
}

export default function LogoutButton({ 
  variant = 'ghost', 
  size = 'default',
  showIcon = true,
  className 
}: LogoutButtonProps) {
  const { logout, user } = useAuth()

  const handleLogout = () => {
    logout()
  }

  return (
    <Button
      variant={variant}
      size={size}
      onClick={handleLogout}
      className={className}
      title={`Logout ${user?.firstName || 'User'}`}
    >
      {showIcon && <LogOut className="h-4 w-4 mr-2" />}
      Logout
    </Button>
  )
}
