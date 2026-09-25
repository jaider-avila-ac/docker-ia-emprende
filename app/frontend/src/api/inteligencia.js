import { api } from './client'

const metaADatos = (meta) => ({
  titulo: meta.titulo,
  especifico: meta.especifico || null,
  numeroMeta: meta.numeroMeta || null,
  fechaLimite: meta.fechaLimite || null,
  medicion: meta.medicion || null,
  pasos: meta.pasos || null,
})

export const inteligenciaApi = {
  listarFoda: () => api.get('/inteligencia/foda'),
  generarFoda: () => api.post('/inteligencia/foda/generar'),
  actualizarFoda: (id, contenido) => api.put(`/inteligencia/foda/${id}`, { contenido }),
  eliminarFoda: (id) => api.del(`/inteligencia/foda/${id}`),

  listarSmart: () => api.get('/inteligencia/smart'),
  generarSmart: () => api.post('/inteligencia/smart/generar'),
  actualizarSmart: (id, meta) => api.put(`/inteligencia/smart/${id}`, metaADatos(meta)),
  eliminarSmart: (id) => api.del(`/inteligencia/smart/${id}`),

  generarIniciativas: () => api.post('/inteligencia/iniciativas/generar'),
  generarPlan: () => api.post('/inteligencia/plan/generar'),
  sugerirBranding: () => api.post('/inteligencia/branding/sugerir'),
  analizar: () => api.post('/inteligencia/analisis'),
}
