import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import Button from '../../components/ui/Button'
import Card from '../../components/ui/Card'
import { Field, Input, Select } from '../../components/ui/Field'
import { useAuth } from '../../context/AuthContext'
import { useBusiness } from '../../context/BusinessContext'
import { negociosApi } from '../../api/negocios'
import { ApiError } from '../../api/client'

const MAXIMO_NEGOCIOS = 3

export default function NegociosPage() {
  const navigate = useNavigate()
  const { usuario, logout } = useAuth()
  const { negocios, estado, entrar, crearNegocio, eliminarNegocio } = useBusiness()
  const [rubros, setRubros] = useState([])
  const [creando, setCreando] = useState(false)
  const [form, setForm] = useState({ nombre: '', rubro: '' })
  const [errors, setErrors] = useState({})
  const [guardando, setGuardando] = useState(false)
  const [entrandoId, setEntrandoId] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    negociosApi
      .rubrosDisponibles()
      .then(setRubros)
      .catch(() => setRubros([]))
  }, [])

  const mensajeDe = (err, porDefecto) => (err instanceof ApiError ? err.message : porDefecto)

  const entrarA = async (id) => {
    setError('')
    setEntrandoId(id)
    try {
      await entrar(id)
      navigate('/', { replace: true })
    } catch (err) {
      setError(mensajeDe(err, 'No se pudo entrar al negocio.'))
      setEntrandoId(null)
    }
  }

  const crear = async (e) => {
    e.preventDefault()
    setError('')
    const nextErrors = {}
    if (!form.nombre.trim()) nextErrors.nombre = 'Escribe el nombre.'
    if (!form.rubro) nextErrors.rubro = 'Elige un rubro.'
    setErrors(nextErrors)
    if (Object.keys(nextErrors).length > 0) return

    setGuardando(true)
    try {
      await crearNegocio({ nombre: form.nombre.trim(), rubro: form.rubro })
      navigate('/negocio', { replace: true })
    } catch (err) {
      setError(mensajeDe(err, 'No se pudo crear el negocio.'))
      setGuardando(false)
    }
  }

  const eliminar = async (negocio) => {
    if (!window.confirm(`Se eliminará "${negocio.nombre}" con todos sus datos. ¿Continuar?`)) return
    setError('')
    try {
      await eliminarNegocio(negocio.id)
    } catch (err) {
      setError(mensajeDe(err, 'No se pudo eliminar el negocio.'))
    }
  }

  const cerrarSesion = () => {
    logout()
    navigate('/login', { replace: true })
  }

  return (
    <div className="min-h-screen bg-gray-100 text-gray-900">
      <header className="bg-indigo-950 text-white px-6 py-3 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="h-8 w-8 rounded-lg grid place-content-center font-semibold text-sm bg-sky-600">IA</div>
          <p className="text-sm font-semibold">IAEmprender</p>
        </div>
        <div className="flex items-center gap-4 text-xs">
          {usuario && <span className="text-indigo-200">{usuario.nombre} {usuario.apellido}</span>}
          <button type="button" onClick={cerrarSesion} className="text-indigo-300 hover:text-white hover:underline">
            Cerrar sesión
          </button>
        </div>
      </header>

      <main className="max-w-4xl mx-auto p-6 space-y-6">
        <div>
          <h1 className="text-xl font-semibold">Tus negocios</h1>
          <p className="text-sm text-gray-500 mt-1">
            Elige con cuál quieres trabajar. Puedes tener hasta {MAXIMO_NEGOCIOS}.
          </p>
        </div>

        {error && <p className="text-xs text-rose-700 bg-rose-50 border border-rose-200 rounded-lg px-3 py-2">{error}</p>}

        {estado === 'cargando' ? (
          <p className="text-sm text-gray-500">Cargando…</p>
        ) : estado === 'error' ? (
          <p className="text-sm text-rose-700">No se pudieron cargar tus negocios.</p>
        ) : (
          <section className="grid grid-cols-1 md:grid-cols-3 gap-4">
            {negocios.map((n) => (
              <Card key={n.id} as="article" className="flex flex-col">
                <div className="flex-1">
                  <div className="h-10 w-10 rounded-xl grid place-content-center bg-sky-50 text-sky-700 text-lg">
                    <i className="bi bi-shop" aria-hidden="true" />
                  </div>
                  <h2 className="text-base font-semibold mt-3 break-words">{n.nombre}</h2>
                  <p className="text-xs text-gray-500 mt-0.5">{n.rubro}</p>
                  {n.ubicacion && <p className="text-xs text-gray-500 mt-0.5">{n.ubicacion}</p>}
                </div>
                <div className="flex gap-2 mt-4">
                  <Button variant="primary" loading={entrandoId === n.id} onClick={() => entrarA(n.id)} className="flex-1 justify-center">
                    Entrar
                  </Button>
                  <Button variant="danger" onClick={() => eliminar(n)} aria-label={`Eliminar ${n.nombre}`}>
                    <i className="bi bi-trash" aria-hidden="true" />
                  </Button>
                </div>
              </Card>
            ))}

            {negocios.length < MAXIMO_NEGOCIOS &&
              (creando ? (
                <Card as="form" onSubmit={crear} noValidate className="space-y-3">
                  <Field label="Nombre del negocio" error={errors.nombre}>
                    <Input
                      invalid={Boolean(errors.nombre)}
                      value={form.nombre}
                      onChange={(e) => setForm({ ...form, nombre: e.target.value })}
                    />
                  </Field>
                  <Field label="Rubro" error={errors.rubro}>
                    <Select
                      invalid={Boolean(errors.rubro)}
                      value={form.rubro}
                      onChange={(e) => setForm({ ...form, rubro: e.target.value })}
                    >
                      <option value="">Selecciona…</option>
                      {rubros.map((r) => (
                        <option key={r}>{r}</option>
                      ))}
                    </Select>
                  </Field>
                  <div className="flex gap-2">
                    <Button variant="success" type="submit" loading={guardando} className="flex-1 justify-center">
                      Crear
                    </Button>
                    <Button variant="subtle" type="button" onClick={() => setCreando(false)}>
                      Cancelar
                    </Button>
                  </div>
                </Card>
              ) : (
                <button
                  type="button"
                  onClick={() => setCreando(true)}
                  className="min-h-40 rounded-2xl border-2 border-dashed border-gray-300 text-gray-500 hover:border-sky-400 hover:text-sky-700 flex flex-col items-center justify-center gap-1 text-sm"
                >
                  <i className="bi bi-plus-lg text-2xl" aria-hidden="true" />
                  Nuevo negocio
                </button>
              ))}
          </section>
        )}
      </main>
    </div>
  )
}
