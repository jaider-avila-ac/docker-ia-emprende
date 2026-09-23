import { api } from './client'

const aDatos = (competidor) => ({
  nombre: competidor.nombre,
  canal: competidor.canal || null,
  notas: competidor.notas || null,
})

export const competidoresApi = {
  listar: () => api.get('/competidores'),
  crear: (competidor) => api.post('/competidores', aDatos(competidor)),
  actualizar: (id, competidor) => api.put(`/competidores/${id}`, aDatos(competidor)),
  eliminar: (id) => api.del(`/competidores/${id}`),
}
