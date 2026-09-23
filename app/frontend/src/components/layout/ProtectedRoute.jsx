import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

export default function ProtectedRoute() {
  const { estado } = useAuth()
  const location = useLocation()

  if (estado === 'cargando') {
    return <div className="min-h-screen grid place-content-center text-sm text-gray-500">Cargando…</div>
  }
  if (estado === 'anonimo') {
    return <Navigate to="/login" replace state={{ desde: location.pathname }} />
  }
  return <Outlet />
}

export function RutaPublica() {
  const { estado } = useAuth()
  if (estado === 'autenticado') return <Navigate to="/" replace />
  return <Outlet />
}
