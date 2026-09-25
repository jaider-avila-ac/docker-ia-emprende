import { useEffect, useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { apikeysApi } from '../../api/apikeys'

export default function AvisoClaveIa() {
  const { pathname } = useLocation()
  const [sinClave, setSinClave] = useState(false)

  useEffect(() => {
    if (pathname === '/configuracion') {
      setSinClave(false)
      return
    }
    let cancelado = false
    apikeysApi
      .listar()
      .then((claves) => {
        if (!cancelado) setSinClave(!claves.some((c) => c.estado === 'activa'))
      })
      .catch(() => {
        if (!cancelado) setSinClave(false)
      })
    return () => {
      cancelado = true
    }
  }, [pathname])

  if (!sinClave) return null

  return (
    <div className="flex flex-wrap items-center gap-3 bg-amber-50 border border-amber-200 text-amber-900 rounded-xl px-4 py-3 text-sm">
      <i className="bi bi-key-fill text-amber-600" aria-hidden="true" />
      <p className="flex-1 min-w-60">
        Todavía no configuras tu clave de IA. Sin ella no puedes generar el FODA ni las metas SMART. Puedes usar una
        clave de OpenAI, Gemini o DeepSeek.
      </p>
      <Link
        to="/configuracion"
        className="rounded-lg bg-amber-500 hover:bg-amber-600 text-white px-3 py-2 text-xs font-medium"
      >
        Configurar ahora
      </Link>
    </div>
  )
}
