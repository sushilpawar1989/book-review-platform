import { Outlet } from 'react-router-dom'

export default function AdminLayout() {
  return (
    <div className="min-h-screen bg-gray-50">
      <div className="container mx-auto px-4 py-8">
        <h1 className="text-2xl font-bold mb-6">Admin Panel</h1>
        <p className="text-gray-600 mb-8">This is a placeholder for the admin layout.</p>
        <Outlet />
      </div>
    </div>
  )
}
