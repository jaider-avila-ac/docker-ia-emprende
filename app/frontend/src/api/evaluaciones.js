import { api } from './client'

export const evaluacionesApi = {
  listar: () => api.get('/evaluaciones'),
  crear: (evaluacion) =>
    api.post('/evaluaciones', {
      iniciativaId: Number(evaluacion.iniciativaId),
      semanaNumero: evaluacion.semanaNumero === '' ? null : Number(evaluacion.semanaNumero),
      seLogro: evaluacion.seLogro,
      dificultad: evaluacion.dificultad,
      repetiria: evaluacion.repetiria,
      comentarios: evaluacion.comentarios || null,
    }),
  eliminar: (id) => api.del(`/evaluaciones/${id}`),
}
