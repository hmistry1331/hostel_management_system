import { Navigate } from 'react-router-dom'
import { rolePath, useAuthStore } from '../store/authStore'

export default function ProtectedRoute({ role, children }) {
  const user = useAuthStore((state) => state.user)
  if (!user) return <Navigate to="/" replace />
  if (role && user.role !== role) return <Navigate to={rolePath(user.role)} replace />
  return children
}
