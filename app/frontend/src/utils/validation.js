export function validateRequired(values, requiredFields) {
  const errors = {}
  requiredFields.forEach((field) => {
    const value = values[field]
    if (value === undefined || value === null || String(value).trim() === '') {
      errors[field] = 'Este campo es obligatorio.'
    }
  })
  return errors
}

export const isValidEmail = (value) => /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value)
