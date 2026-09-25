import { api } from './client'

const aDatos = (negocio) => ({
  nombre: negocio.nombre,
  rubro: negocio.rubro,
  ubicacion: negocio.ubicacion || null,
  vendeEnLinea: Boolean(negocio.vendeEnLinea),
  coberturaEnvio: negocio.coberturaEnvio || null,
  descripcion: negocio.descripcion || null,
  publicoObjetivo: negocio.publicoObjetivo || null,
  diferenciador: negocio.diferenciador || null,
  tono: negocio.tono || null,
  costosFijosMensuales: negocio.costosFijosMensuales ?? null,
  redesActivas: negocio.redesActivas || [],
  pilares: negocio.pilares || [],
})

export const negociosApi = {
  listar: () => api.get('/negocios'),

  obtenerActivo: () => api.get('/negocios/activo'),
  crear: (negocio) => api.post('/negocios', aDatos(negocio)),
  actualizar: (id, negocio) => api.put(`/negocios/${id}`, aDatos(negocio)),
  activar: (id) => api.put(`/negocios/${id}/activar`),
  eliminar: (id) => api.del(`/negocios/${id}`),

  rubrosDisponibles: () => api.get('/negocios/rubros'),
}
