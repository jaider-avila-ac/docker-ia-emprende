import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import PageHeader from '../../components/ui/PageHeader'
import Card from '../../components/ui/Card'
import Button from '../../components/ui/Button'
import { AiBadge, UserBadge } from '../../components/ui/Badge'
import { Field, Select } from '../../components/ui/Field'
import { planApi } from '../../api/plan'
import { ApiError } from '../../api/client'

const CONSEJOS = {
  Poco: 'Con poco tiempo, prioriza calidad sobre cantidad: pocas publicaciones bien hechas rinden más que varias improvisadas.',
  Medio: 'Con este tiempo puedes sostener un ritmo constante sin agotarte — la constancia es lo que más importa.',
  Bastante: 'Puedes aprovechar para probar formatos nuevos, como videos cortos mostrando el detrás de cámaras.',
}

export default function AjustesPlanPage() {
  const navigate = useNavigate()
  const [plan, setPlan] = useState(null)
  const [nivel, setNivel] = useState('Medio')
  const [cargando, setCargando] = useState(true)
  const [guardando, setGuardando] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    planApi
      .actual()
      .then((p) => {
        setPlan(p)
        setNivel(p.tiempoDisponible)
      })
      .catch((err) => setError(err instanceof ApiError ? err.message : 'No se pudo cargar el plan.'))
      .finally(() => setCargando(false))
  }, [])

  const onChangeNivel = async (e) => {
    const value = e.target.value
    setNivel(value)
  }

  const guardar = async () => {
    setError('')
    setGuardando(true)
    try {
      const actualizado = await planApi.ajustar(nivel)
      setPlan(actualizado)
      navigate('/plan')
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'No se pudo guardar el ajuste.')
    } finally {
      setGuardando(false)
    }
  }

  if (cargando) return <p className="text-sm text-gray-500">Cargando…</p>

  return (
    <>
      <PageHeader
        title="Ajustes del plan"
        description={
          <>
            Tú dices cuánto tiempo tienes; la IA te sugiere la cadencia. <UserBadge className="ml-1" />
          </>
        }
      />

      {error && <p className="text-xs text-rose-700 bg-rose-50 border border-rose-200 rounded-lg px-3 py-2">{error}</p>}

      <Card className="text-sm space-y-4">
        <Field label="¿Cuánto tiempo puedes dedicarle a tu presencia digital esta semana?" className="max-w-sm">
          <Select value={nivel} onChange={onChangeNivel}>
            <option value="Poco">Poco (1-2 horas a la semana)</option>
            <option value="Medio">Medio (3-5 horas a la semana)</option>
            <option value="Bastante">Bastante (6+ horas a la semana)</option>
          </Select>
        </Field>

        <div className="bg-sky-50 border border-sky-200 rounded-xl p-4">
          <p className="text-[11px] text-sky-700 font-medium flex items-center gap-2">
            Sugerencia <AiBadge />
          </p>
          {plan && plan.tiempoDisponible === nivel ? (
            <>
              <div className="mt-2 grid grid-cols-2 sm:grid-cols-3 gap-3">
                <div>
                  <p className="text-xl font-semibold text-gray-900">{plan.publicacionesSugeridas}</p>
                  <p className="text-xs text-gray-500">Publicaciones/semana</p>
                </div>
                <div>
                  <p className="text-xl font-semibold text-gray-900">{plan.historiasSugeridas}</p>
                  <p className="text-xs text-gray-500">Historias/semana</p>
                </div>
                <div>
                  <p className="text-xl font-semibold text-gray-900">{plan.ventanaHoraria}</p>
                  <p className="text-xs text-gray-500">Mejor ventana horaria</p>
                </div>
              </div>
              <p className="text-gray-700 mt-3">{CONSEJOS[nivel]}</p>
            </>
          ) : (
            <p className="mt-2 text-gray-500">Guarda para ver la sugerencia para "{nivel}".</p>
          )}
        </div>

        <div className="flex items-center gap-2">
          <Button variant="success" loading={guardando} onClick={guardar}>
            {guardando ? 'Guardando…' : 'Guardar y volver'}
          </Button>
          <Button to="/plan" variant="subtle">Cancelar</Button>
          <Button to="/iniciativas" variant="warning" className="ml-auto">Ir a Iniciativas</Button>
        </div>
      </Card>
    </>
  )
}
