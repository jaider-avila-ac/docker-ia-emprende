import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import PageHeader from '../../components/ui/PageHeader'
import Card from '../../components/ui/Card'
import Button from '../../components/ui/Button'
import StatTile from '../../components/ui/StatTile'
import NoteBox from '../../components/ui/NoteBox'
import { ESTADO_COLOR } from '../iniciativas/data'
import { iniciativasApi } from '../../api/iniciativas'
import ErrorIa from '../../components/ui/ErrorIa'
import { AiBadge } from '../../components/ui/Badge'
import { planApi } from '../../api/plan'
import { inteligenciaApi } from '../../api/inteligencia'
import { ApiError } from '../../api/client'

export default function PlanPage() {
  const navigate = useNavigate()
  const [generando, setGenerando] = useState(false)
  const [plan, setPlan] = useState(null)
  const [iniciativas, setIniciativas] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    setCargando(true)
    Promise.all([planApi.actual(), iniciativasApi.listar()])
      .then(([plan, iniciativas]) => {
        setPlan(plan)
        setIniciativas(iniciativas)
      })
      .catch((err) => setError(err instanceof ApiError ? err.message : 'No se pudo cargar el plan.'))
      .finally(() => setCargando(false))
  }, [])

  const generarPlan = async () => {
    setError('')
    setGenerando(true)
    try {
      const generado = await inteligenciaApi.generarPlan()
      navigate(`/plan/semana/${generado.semanaNumero}`)
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'No se pudo generar el plan.')
      setGenerando(false)
    }
  }

  const focoSemana = iniciativas.filter((i) => ['En prueba', 'Aprobada'].includes(i.estado))

  if (cargando) return <p className="text-sm text-gray-500">Cargando…</p>

  return (
    <>
      <PageHeader title="Plan semanal" description={<AiBadge />}>
        <Button variant="success" loading={generando} onClick={generarPlan}>
          {generando ? 'Generando… (puede tardar hasta 1 min)' : 'Generar plan de la semana con IA'}
        </Button>
        <Button to="/iniciativas" variant="warning">Ir a Iniciativas</Button>
        <Button to="/plan/ajustes" variant="neutral">Ajustes del plan</Button>
        {plan && <Button to={`/plan/semana/${plan.semanaNumero}`} variant="primary">Ver semana {plan.semanaNumero}</Button>}
      </PageHeader>

      <ErrorIa mensaje={error} />

      {plan && (
        <section className="grid grid-cols-1 md:grid-cols-3 gap-3">
          <StatTile color="amber" value={`Sem. ${plan.semanaNumero}`} label="Semana vigente" />
          <StatTile color="sky" value={plan.ventanaHoraria || '—'} label="Ventana recomendada" />
          <StatTile color="emerald" value={`${plan.publicacionesSugeridas} · ${plan.historiasSugeridas}`} label="Publicaciones · Historias por semana" />
        </section>
      )}

      <section className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <Card>
          <div className="flex items-center justify-between">
            <h2 className="text-base font-semibold">Iniciativas foco</h2>
            <Button to="/iniciativas" variant="warning" size="xs">Gestionar</Button>
          </div>
          {focoSemana.length === 0 ? (
            <p className="text-sm text-gray-500 mt-3">
              Ninguna iniciativa está "En prueba" o "Aprobada" todavía — actívalas en Iniciativas.
            </p>
          ) : (
            <ul className="mt-3 text-sm text-gray-800 space-y-2">
              {focoSemana.map((i) => (
                <li key={i.id} className="flex items-start justify-between gap-3">
                  <div>
                    <p className="font-medium">{i.titulo}</p>
                    <p className="text-[11px] text-gray-500">
                      Estado: <span className={ESTADO_COLOR[i.estado]}>{i.estado}</span> · ICE {i.ice.toFixed(1)}
                    </p>
                  </div>
                  <Button to={`/iniciativas/${i.id}`} variant="ghostSky" size="xs">Ver</Button>
                </li>
              ))}
            </ul>
          )}
        </Card>
      </section>

      <NoteBox>
        La IA arma el plan de la semana a partir de tus{' '}
        <a className="text-amber-700 underline" href="/iniciativas">iniciativas priorizadas</a> y de lo que aprendiste
        en tus evaluaciones. Después puedes editar o quitar las acciones, o dejarlas tal cual.
      </NoteBox>
    </>
  )
}
