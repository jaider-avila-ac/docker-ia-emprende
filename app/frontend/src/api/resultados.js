import { api } from './client'

const aDatos = (resultado) => ({
  anio: Number(resultado.anio),
  semanaNumero: Number(resultado.semanaNumero),
  ingresosAprox: Number(resultado.ingresosAprox),
  clientesAprox: resultado.clientesAprox === '' || resultado.clientesAprox == null ? null : Number(resultado.clientesAprox),
})

export const resultadosApi = {
  listar: () => api.get('/resultados'),
  crear: (resultado) => api.post('/resultados', aDatos(resultado)),
  actualizar: (id, resultado) => api.put(`/resultados/${id}`, aDatos(resultado)),
  eliminar: (id) => api.del(`/resultados/${id}`),
}
