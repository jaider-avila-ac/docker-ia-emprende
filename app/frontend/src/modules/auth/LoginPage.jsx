import { useState } from 'react'
import { useLocation, useNavigate, Link } from 'react-router-dom'
import Button from '../../components/ui/Button'
import { Field, Input } from '../../components/ui/Field'
import { validateRequired, isValidEmail } from '../../utils/validation'
import { useAuth } from '../../context/AuthContext'
import { ApiError } from '../../api/client'

export default function LoginPage() {
  const navigate = useNavigate()
  const location = useLocation()
  const { login } = useAuth()
  const [form, setForm] = useState({ email: '', password: '' })
  const [errors, setErrors] = useState({})
  const [errorGeneral, setErrorGeneral] = useState('')
  const [loading, setLoading] = useState(false)

  const onSubmit = async (e) => {
    e.preventDefault()
    setErrorGeneral('')

    const nextErrors = validateRequired(form, ['email', 'password'])
    if (!nextErrors.email && !isValidEmail(form.email)) {
      nextErrors.email = 'Ingresa un correo válido.'
    }
    setErrors(nextErrors)
    if (Object.keys(nextErrors).length > 0) return

    setLoading(true)
    try {
      await login(form.email, form.password)
      navigate(location.state?.desde || '/', { replace: true })
    } catch (err) {
      setErrorGeneral(err instanceof ApiError ? err.message : 'No se pudo iniciar sesión.')
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
          <p className="text-xs text-gray-500">Ingresa para continuar con tu negocio</p>
        </div>

        <form onSubmit={onSubmit} noValidate className="bg-white border border-gray-200 rounded-2xl p-5 space-y-3 text-sm">
          {location.state?.cuentaCreada && !errorGeneral && (
            <p className="text-xs text-emerald-700 bg-emerald-50 border border-emerald-200 rounded-lg px-3 py-2">
              Cuenta creada — ya puedes iniciar sesión.
            </p>
          )}
          {errorGeneral && (
            <p className="text-xs text-rose-700 bg-rose-50 border border-rose-200 rounded-lg px-3 py-2">{errorGeneral}</p>
          )}
          <Field label="Correo electrónico" error={errors.email}>
            <Input
              type="email"
              focus="sky"
              invalid={Boolean(errors.email)}
              placeholder="tucorreo@ejemplo.com"
              value={form.email}
              onChange={(e) => setForm({ ...form, email: e.target.value })}
            />
          </Field>
          <Field label="Contraseña" error={errors.password}>
            <Input
              type="password"
              focus="sky"
              invalid={Boolean(errors.password)}
              placeholder="••••••••"
              value={form.password}
              onChange={(e) => setForm({ ...form, password: e.target.value })}
            />
          </Field>

          <Button type="submit" variant="primary" loading={loading} className="w-full justify-center">
            {loading ? 'Entrando…' : 'Entrar'}
          </Button>

          <p className="text-center text-xs text-gray-500 pt-1">
            ¿No tienes cuenta?{' '}
            <Link to="/registro" className="text-sky-700 hover:underline">Regístrate</Link>
          </p>
        </form>
      </div>
    </div>
  )
}
