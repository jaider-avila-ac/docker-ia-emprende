import { api } from './client'

const aDatos = (oferta) => ({
  tipo: oferta.tipo,
  nombre: oferta.nombre,
  categoria: oferta.categoria || null,
  precio: oferta.precio === '' || oferta.precio === null || oferta.precio === undefined ? null : Number(oferta.precio),
  destacar: oferta.destacar || null,
})

export const ofertasApi = {
  listar: () => api.get('/ofertas'),
  crear: (oferta) => api.post('/ofertas', aDatos(oferta)),
  actualizar: (id, oferta) => api.put(`/ofertas/${id}`, aDatos(oferta)),
  eliminar: (id) => api.del(`/ofertas/${id}`),
}
