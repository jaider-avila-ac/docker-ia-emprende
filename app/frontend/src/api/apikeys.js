import { api } from './client'

export const apikeysApi = {
  listar: () => api.get('/api-keys'),
  guardar: (proveedor, clave) => api.put(`/api-keys/${proveedor}`, { clave }),
  eliminar: (proveedor) => api.del(`/api-keys/${proveedor}`),
}
