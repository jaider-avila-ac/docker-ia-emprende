import { api } from './client'

export const authApi = {
  registrar: (datos) =>
    api.post('/usuarios', {
      correo: datos.correo,
      password: datos.password,
      nombre: datos.nombre,
      apellido: datos.apellido,
      fechaNacimiento: datos.fechaNacimiento,
    }),

  login: (correo, password) => api.post('/auth/login', { correo, password }),

  yo: () => api.get('/usuarios/yo'),
}
