import { api } from './client'

export const planApi = {

  actual: () => api.get('/plan/actual'),
  ajustar: (tiempoDisponible) => api.put('/plan/ajustes', { tiempoDisponible }),

  semana: (numero, anio) => api.get(`/plan/semana/${numero}${anio ? `?anio=${anio}` : ''}`),
  agregarAccion: (numero, accion, anio) =>
    api.post(`/plan/semana/${numero}/acciones${anio ? `?anio=${anio}` : ''}`, accion),
  actualizarAccion: (id, accion) => api.put(`/plan/acciones/${id}`, accion),
  eliminarAccion: (id) => api.del(`/plan/acciones/${id}`),
}
