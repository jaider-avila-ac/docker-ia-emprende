import { createContext, useCallback, useContext, useEffect, useState } from 'react'
import { negociosApi } from '../api/negocios'
import { useAuth } from './AuthContext'

const BusinessContext = createContext(null)

const CLAVE_NEGOCIO = 'negocioEntrado'

const leerNegocioGuardado = () => {
  try {
    return Number(sessionStorage.getItem(CLAVE_NEGOCIO)) || null
  } catch {
    return null
  }
}

const guardarNegocio = (id) => {
  try {
    if (id) sessionStorage.setItem(CLAVE_NEGOCIO, String(id))
    else sessionStorage.removeItem(CLAVE_NEGOCIO)
  } catch {
    return
  }
}

export function BusinessProvider({ children }) {
  const { estado: estadoAuth } = useAuth()
  const [negocios, setNegocios] = useState([])
  const [business, setBusiness] = useState(null)
  const [estado, setEstado] = useState('cargando')

  const recargar = useCallback(async () => {
    setEstado('cargando')
    try {
      const lista = await negociosApi.listar()
      setNegocios(lista)
      const guardado = leerNegocioGuardado()
      const dentro = lista.find((n) => n.id === guardado)
      if (dentro) {
        const listo = dentro.activo ? dentro : await negociosApi.activar(dentro.id)
        setBusiness(listo)
      } else {
        setBusiness(null)
        guardarNegocio(null)
      }
      setEstado('listo')
    } catch {
      setEstado('error')
    }
  }, [])

  useEffect(() => {
    if (estadoAuth === 'autenticado') {
      recargar()
    } else if (estadoAuth === 'anonimo') {
      setBusiness(null)
      setNegocios([])
      guardarNegocio(null)
      setEstado('cargando')
    }
  }, [estadoAuth, recargar])

  const entrar = async (id) => {
    const activado = await negociosApi.activar(id)
    guardarNegocio(activado.id)
    setNegocios((prev) => prev.map((n) => ({ ...n, activo: n.id === activado.id })))
    setBusiness(activado)
    return activado
  }

  const salir = () => {
    guardarNegocio(null)
    setBusiness(null)
  }

  const crearNegocio = async (datos) => {
    const creado = await negociosApi.crear(datos)
    setNegocios((prev) => [...prev, creado])
    return entrar(creado.id)
  }

  const actualizarNegocio = async (datos) => {
    const actualizado = await negociosApi.actualizar(business.id, datos)
    setBusiness(actualizado)
    setNegocios((prev) => prev.map((n) => (n.id === actualizado.id ? actualizado : n)))
    return actualizado
  }

  const eliminarNegocio = async (id) => {
    await negociosApi.eliminar(id)
    setNegocios((prev) => prev.filter((n) => n.id !== id))
    if (business?.id === id) salir()
  }

  const updateBusinessLocal = (patch) => setBusiness((prev) => (prev ? { ...prev, ...patch } : prev))

  return (
    <BusinessContext.Provider
      value={{
        negocios,
        business,
        estado,
        entrar,
        salir,
        crearNegocio,
        actualizarNegocio,
        eliminarNegocio,
        updateBusinessLocal,
        recargar,
      }}
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
