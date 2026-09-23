export const money = (value) => (value || value === 0 ? `$${Number(value).toLocaleString('es-CO')}` : '—')
