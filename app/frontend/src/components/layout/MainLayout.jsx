import { useEffect, useState } from 'react'
import { Outlet, useLocation } from 'react-router-dom'
import Sidebar from './Sidebar'
import AvisoClaveIa from './AvisoClaveIa'

export default function MainLayout() {
  const [mobileOpen, setMobileOpen] = useState(false)
  const location = useLocation()

  useEffect(() => {
    setMobileOpen(false)
  }, [location.pathname])

  return (
    <div className="min-h-screen md:grid md:grid-cols-[260px_1fr] bg-gray-100 text-gray-900">
      <div className="md:hidden sticky top-0 z-30 flex items-center gap-3 bg-indigo-950 text-white px-4 py-3">
        <button
          type="button"
          onClick={() => setMobileOpen(true)}
          aria-label="Abrir menú"
          className="p-1.5 -ml-1.5 rounded-lg hover:bg-indigo-800/60"
        >
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" className="w-6 h-6">
            <path strokeLinecap="round" strokeLinejoin="round" d="M4 6h16M4 12h16M4 18h16" />
          </svg>
        </button>
        <div className="h-7 w-7 rounded-lg grid place-content-center font-semibold text-xs bg-sky-600">IA</div>
        <p className="text-sm font-semibold">IAEmprender</p>
      </div>

      {mobileOpen && (
        <div
          className="fixed inset-0 z-40 bg-black/40 md:hidden"
          onClick={() => setMobileOpen(false)}
          aria-hidden="true"
        />
      )}

      <div
        className={`fixed inset-y-0 left-0 z-50 w-72 max-w-[85vw] transform transition-transform duration-200 ease-out
          md:static md:z-auto md:w-auto md:max-w-none md:transform-none
          ${mobileOpen ? 'translate-x-0' : '-translate-x-full md:translate-x-0'}`}
      >
        <Sidebar onNavigate={() => setMobileOpen(false)} />
      </div>

      <main className="p-4 sm:p-6 space-y-6 min-w-0">
        <AvisoClaveIa />
        <Outlet />
      </main>
    </div>
  )
}
