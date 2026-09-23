import { createContext, useCallback, useContext, useEffect, useState } from 'react'
import { negociosApi } from '../api/negocios'
import { ApiError } from '../api/client'
import { useAuth } from './AuthContext'

const BusinessContext = createContext(null)

export function BusinessProvider({ children }) {
  const { estado: estadoAuth } = useAuth()
  const [business, setBusiness] = useState(null)

  const [estado, setEstado] = useState('cargando')

  const recargar = useCallback(async () => {
    setEstado('cargando')
    try {
      const negocio = await negociosApi.obtenerActivo()
      setBusiness(negocio)
      setEstado('listo')
    } catch (err) {
      if (err instanceof ApiError && err.estado === 404) {
        setBusiness(null)
        setEstado('sin-negocio')
      } else {
        setEstado('error')
      }
    }
  }, [])

  useEffect(() => {
    if (estadoAuth === 'autenticado') {
      recargar()
    } else if (estadoAuth === 'anonimo') {
      setBusiness(null)
      setEstado('cargando')
    }
  }, [estadoAuth, recargar])

  const crearNegocio = async (datos) => {
    const creado = await negociosApi.crear(datos)
    setBusiness(creado)
    setEstado('listo')
    return creado
  }

  const actualizarNegocio = async (datos) => {
    const actualizado = await negociosApi.actualizar(business.id, datos)
    setBusiness(actualizado)
    return actualizado
  }

  const updateBusinessLocal = (patch) => setBusiness((prev) => (prev ? { ...prev, ...patch } : prev))

  return (
    <BusinessContext.Provider
      value={{ business, estado, crearNegocio, actualizarNegocio, updateBusinessLocal, recargar }}
    >
      {children}
    </BusinessContext.Provider>
  )
}

export function useBusiness() {
  const ctx = useContext(BusinessContext)
  if (!ctx) throw new Error('useBusiness debe usarse dentro de <BusinessProvider>')
  return ctx
}
