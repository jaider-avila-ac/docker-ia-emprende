const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

const TOKEN_KEY = 'iaemprender_token'

export const tokenStorage = {
  obtener: () => localStorage.getItem(TOKEN_KEY),
  guardar: (token) => localStorage.setItem(TOKEN_KEY, token),
  borrar: () => localStorage.removeItem(TOKEN_KEY),
}

export class ApiError extends Error {
  constructor(estado, mensaje, detalles) {
    super(mensaje)
    this.estado = estado
    this.detalles = detalles || []
  }
}

export class SesionExpiradaError extends ApiError {}

async function solicitud(metodo, ruta, cuerpo) {
  const headers = { 'Content-Type': 'application/json; charset=utf-8' }
  const token = tokenStorage.obtener()
  if (token) headers.Authorization = `Bearer ${token}`

  let respuesta
  try {
    respuesta = await fetch(`${BASE_URL}${ruta}`, {
      method: metodo,
      headers,
      body: cuerpo !== undefined ? JSON.stringify(cuerpo) : undefined,
    })
  } catch {
    throw new ApiError(0, 'No se pudo contactar al servidor. Revisa tu conexión.')
  }

  if (respuesta.status === 204) return null

  const texto = await respuesta.text()
  const datos = texto ? JSON.parse(texto) : null

  if (!respuesta.ok) {
    const mensaje = datos?.mensaje || `Error inesperado (${respuesta.status}).`

    if (respuesta.status === 401 || respuesta.status === 403) {
      tokenStorage.borrar()
      throw new SesionExpiradaError(respuesta.status, mensaje, datos?.detalles)
    }
    throw new ApiError(respuesta.status, mensaje, datos?.detalles)
  }

  return datos
}

export const api = {
  get: (ruta) => solicitud('GET', ruta),
  post: (ruta, cuerpo) => solicitud('POST', ruta, cuerpo ?? {}),
  put: (ruta, cuerpo) => solicitud('PUT', ruta, cuerpo ?? {}),
  del: (ruta) => solicitud('DELETE', ruta),
}
