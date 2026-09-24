import { createContext, useContext, useEffect, useState } from 'react'
import { authApi } from '../api/auth'
import { tokenStorage } from '../api/client'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(null)

  const [estado, setEstado] = useState(tokenStorage.obtener() ? 'cargando' : 'anonimo')

  useEffect(() => {
    if (estado !== 'cargando') return
    authApi
      .yo()
      .then((datos) => {
        setUsuario(datos)
        setEstado('autenticado')
      })
      .catch(() => {
        setUsuario(null)
        setEstado('anonimo')
      })
  }, [estado])

  const login = async (correo, password) => {
    const { token } = await authApi.login(correo, password)
    tokenStorage.guardar(token)
    const datos = await authApi.yo()
    setUsuario(datos)
    setEstado('autenticado')
  }

  const registrar = (datos) => authApi.registrar(datos)

  const logout = () => {
    tokenStorage.borrar()
    setUsuario(null)
    setEstado('anonimo')
  }

  return (
    <AuthContext.Provider value={{ usuario, estado, login, registrar, logout }}>{children}</AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth debe usarse dentro de <AuthProvider>')
  return ctx
}
