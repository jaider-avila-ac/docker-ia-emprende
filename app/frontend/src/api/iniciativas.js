import { api } from './client'

const aDatos = (iniciativa) => ({
  titulo: iniciativa.titulo,
  metaTipo: iniciativa.metaTipo || null,
  pilar: iniciativa.pilar || null,
  estado: iniciativa.estado || null,
  impacto: Number(iniciativa.impacto),
  confianza: Number(iniciativa.confianza),
  esfuerzo: Number(iniciativa.esfuerzo),
  descripcion: iniciativa.descripcion || null,
  notas: iniciativa.notas || null,
  ofertaIds: (iniciativa.ofertaIds || []).map(Number),
})

export const iniciativasApi = {
  listar: () => api.get('/iniciativas'),
  crear: (iniciativa) => api.post('/iniciativas', aDatos(iniciativa)),
  actualizar: (id, iniciativa) => api.put(`/iniciativas/${id}`, aDatos(iniciativa)),
  eliminar: (id) => api.del(`/iniciativas/${id}`),
}
