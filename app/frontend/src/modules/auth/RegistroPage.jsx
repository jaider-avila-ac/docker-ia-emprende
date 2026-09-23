import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import Button from '../../components/ui/Button'
import { Field, Input } from '../../components/ui/Field'
import { validateRequired, isValidEmail } from '../../utils/validation'
import { useAuth } from '../../context/AuthContext'
import { ApiError } from '../../api/client'

const emptyForm = { correo: '', password: '', nombre: '', apellido: '', fechaNacimiento: '' }

export default function RegistroPage() {
  const navigate = useNavigate()
  const { registrar } = useAuth()
  const [form, setForm] = useState(emptyForm)
  const [errors, setErrors] = useState({})
  const [errorGeneral, setErrorGeneral] = useState('')
  const [loading, setLoading] = useState(false)

  const onChange = (key) => (e) => setForm((prev) => ({ ...prev, [key]: e.target.value }))

  const onSubmit = async (e) => {
    e.preventDefault()
    setErrorGeneral('')

    const nextErrors = validateRequired(form, ['correo', 'password', 'nombre', 'apellido', 'fechaNacimiento'])
    if (!nextErrors.correo && !isValidEmail(form.correo)) nextErrors.correo = 'Ingresa un correo válido.'
    if (!nextErrors.password && form.password.length < 8) {
      nextErrors.password = 'La contraseña debe tener al menos 8 caracteres.'
    }
    setErrors(nextErrors)
    if (Object.keys(nextErrors).length > 0) return

    setLoading(true)
    try {
      await registrar(form)
      navigate('/login', { replace: true, state: { cuentaCreada: true } })
    } catch (err) {
      setErrorGeneral(err instanceof ApiError ? err.message : 'No se pudo crear la cuenta.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-gray-100 flex items-center justify-center p-6">
      <div className="w-full max-w-sm">
        <div className="flex flex-col items-center gap-2 mb-6">
          <div className="h-11 w-11 rounded-xl grid place-content-center font-semibold text-white bg-sky-600">IA</div>
          <p className="text-base font-semibold text-gray-900">IAEmprender</p>
          <p className="text-xs text-gray-500">Crea tu cuenta para empezar</p>
        </div>

        <form onSubmit={onSubmit} noValidate className="bg-white border border-gray-200 rounded-2xl p-5 space-y-3 text-sm">
          {errorGeneral && (
            <p className="text-xs text-rose-700 bg-rose-50 border border-rose-200 rounded-lg px-3 py-2">{errorGeneral}</p>
          )}
          <div className="grid grid-cols-2 gap-3">
            <Field label="Nombre" error={errors.nombre}>
              <Input invalid={Boolean(errors.nombre)} value={form.nombre} onChange={onChange('nombre')} />
            </Field>
            <Field label="Apellido" error={errors.apellido}>
              <Input invalid={Boolean(errors.apellido)} value={form.apellido} onChange={onChange('apellido')} />
            </Field>
          </div>
          <Field label="Correo electrónico" error={errors.correo}>
            <Input
              type="email"
              focus="sky"
              invalid={Boolean(errors.correo)}
              placeholder="tucorreo@ejemplo.com"
              value={form.correo}
              onChange={onChange('correo')}
            />
          </Field>
          <Field label="Contraseña (mínimo 8 caracteres)" error={errors.password}>
            <Input
              type="password"
              focus="sky"
              invalid={Boolean(errors.password)}
              placeholder="••••••••"
              value={form.password}
              onChange={onChange('password')}
            />
          </Field>
          <Field label="Fecha de nacimiento" error={errors.fechaNacimiento}>
            <Input
              type="date"
              focus="sky"
              invalid={Boolean(errors.fechaNacimiento)}
              value={form.fechaNacimiento}
              onChange={onChange('fechaNacimiento')}
            />
          </Field>

          <Button type="submit" variant="success" loading={loading} className="w-full justify-center">
            {loading ? 'Creando cuenta…' : 'Crear cuenta'}
          </Button>

          <p className="text-center text-xs text-gray-500 pt-1">
            ¿Ya tienes cuenta?{' '}
            <Link to="/login" className="text-sky-700 hover:underline">Inicia sesión</Link>
          </p>
        </form>
      </div>
    </div>
  )
}
