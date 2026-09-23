import { api } from './client'

export const inteligenciaApi = {
  listarFoda: () => api.get('/inteligencia/foda'),
  generarFoda: () => api.post('/inteligencia/foda/generar'),
  listarSmart: () => api.get('/inteligencia/smart'),
  generarSmart: () => api.post('/inteligencia/smart/generar'),
}
