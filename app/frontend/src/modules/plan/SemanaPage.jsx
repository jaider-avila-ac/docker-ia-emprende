import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import Card from '../../components/ui/Card'
import Button from '../../components/ui/Button'
import NoteBox from '../../components/ui/NoteBox'
import EmptyState from '../../components/ui/EmptyState'
import ErrorIa from '../../components/ui/ErrorIa'
import { Input, Select } from '../../components/ui/Field'
import { planApi } from '../../api/plan'
import { inteligenciaApi } from '../../api/inteligencia'
import { ApiError } from '../../api/client'

const DIAS = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo']

function Accion({ accion, onGuardar, onQuitar }) {
  const [editando, setEditando] = useState(false)
  const [dia, setDia] = useState(accion.diaSemana)
  const [texto, setTexto] = useState(accion.descripcion)
  const [guardando, setGuardando] = useState(false)

  const guardar = async () => {
    if (!texto.trim()) return
    setGuardando(true)
    const ok = await onGuardar(accion.id, { diaSemana: dia, descripcion: texto.trim() })
    setGuardando(false)
    if (ok) setEditando(false)
  }

  if (editando) {
    return (
      <li className="space-y-2">
        <Select focus="sky" value={dia} onChange={(e) => setDia(e.target.value)}>
          {DIAS.map((d) => <option key={d}>{d}</option>)}
        </Select>
        <Input focus="sky" value={texto} onChange={(e) => setTexto(e.target.value)} />
        <div className="flex gap-2">
          <Button variant="success" size="xs" loading={guardando} onClick={guardar}>Guardar</Button>
          <Button
            variant="subtle"
            size="xs"
            onClick={() => {
              setDia(accion.diaSemana)
              setTexto(accion.descripcion)
              setEditando(false)
            }}
          >
            Cancelar
          </Button>
        </div>
      </li>
    )
  }

  return (
    <li className="flex items-start justify-between gap-2">
      <span>• {accion.descripcion}</span>
      <span className="flex gap-2 shrink-0">
        <button type="button" onClick={() => setEditando(true)} className="text-[11px] text-sky-700 hover:underline">
          Editar
        </button>
        <button type="button" onClick={() => onQuitar(accion.id)} className="text-[11px] text-rose-600 hover:underline">
          Quitar
        </button>
      </span>
    </li>
  )
}

export default function SemanaPage() {
  const { numero } = useParams()
  const [plan, setPlan] = useState(null)
  const [existe, setExiste] = useState(true)
  const [cargando, setCargando] = useState(true)
  const [generando, setGenerando] = useState(false)
  const [error, setError] = useState('')

  const mensajeDe = (err, porDefecto) => (err instanceof ApiError ? err.message : porDefecto)

  const cargar = () => {
    setCargando(true)
    planApi
      .semana(numero)
      .then((p) => {
        setPlan(p)
        setExiste(true)
      })
      .catch((err) => {
        if (err instanceof ApiError && err.estado === 404) setExiste(false)
        else setError(mensajeDe(err, 'No se pudo cargar la semana.'))
      })
      .finally(() => setCargando(false))
  }

  useEffect(cargar, [numero])

  const generar = async () => {
    setError('')
    setGenerando(true)
    try {
      const generado = await inteligenciaApi.generarPlan()
      if (String(generado.semanaNumero) === String(numero)) {
        setPlan(generado)
        setExiste(true)
      } else {
        window.location.assign(`/plan/semana/${generado.semanaNumero}`)
      }
    } catch (err) {
      setError(mensajeDe(err, 'No se pudo generar el plan.'))
    } finally {
      setGenerando(false)
    }
  }

  const guardarAccion = async (id, datos) => {
    setError('')
    try {
      await planApi.actualizarAccion(id, datos)
      cargar()
      return true
    } catch (err) {
      setError(mensajeDe(err, 'No se pudo guardar el cambio.'))
      return false
    }
  }

  const quitarAccion = async (id) => {
    setError('')
    try {
      await planApi.eliminarAccion(id)
      cargar()
    } catch (err) {
      setError(mensajeDe(err, 'No se pudo quitar la acción.'))
    }
  }

  if (cargando) return <p className="text-sm text-gray-500">Cargando…</p>

  if (!existe) {
    return (
      <>
        <Link to="/plan" className="text-xs text-sky-700 hover:underline">← Volver al plan</Link>
        <Card className="mt-3">
          <EmptyState
            title={`Todavía no hay plan para la semana ${numero}`}
            description="Vuelve al plan y genera el de la semana vigente con IA."
            action={<Button to="/plan" variant="success">Ir al plan</Button>}
          />
        </Card>
      </>
    )
  }

  return (
    <>
      <header className="flex flex-wrap items-center justify-between gap-2">
        <div>
          <Link to="/plan" className="text-xs text-sky-700 hover:underline">← Volver al plan</Link>
          <h1 className="text-lg font-semibold mt-1">Semana {plan.semanaNumero} · {plan.anio}</h1>
          <p className="text-xs text-gray-500">Ventana recomendada: {plan.ventanaHoraria || '—'}</p>
        </div>
        <div className="flex flex-wrap gap-2">
          <Button variant="danger" loading={generando} onClick={generar}>
            {generando ? 'Generando…' : 'Regenerar con IA'}
          </Button>
          <Button to="/iniciativas" variant="warning">Ir a Iniciativas</Button>
          <Button to="/evaluacion" variant="success">Evaluar semana</Button>
        </div>
      </header>

      <ErrorIa mensaje={error} />

      {plan.acciones.length === 0 && (
        <Card>
          <EmptyState
            title="Esta semana aún no tiene acciones"
            description="La IA las arma a partir de tus iniciativas priorizadas."
            action={
              <Button variant="success" loading={generando} onClick={generar}>
                {generando ? 'Generando…' : 'Generar plan con IA'}
              </Button>
            }
          />
        </Card>
      )}

      <section className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        {[DIAS.slice(0, 4), DIAS.slice(4)].map((columna, i) => (
          <div key={i} className="space-y-4">
            {columna.map((dia) => {
              const acciones = plan.acciones.filter((a) => a.diaSemana === dia)
              return (
                <Card key={dia} as="article" className="text-sm">
                  <h2 className="text-base font-semibold">{dia}</h2>
                  {acciones.length === 0 ? (
                    <p className="text-gray-400 mt-2 text-xs">Sin acciones.</p>
                  ) : (
                    <ul className="mt-2 text-gray-700 space-y-2">
                      {acciones.map((a) => (
                        <Accion key={a.id} accion={a} onGuardar={guardarAccion} onQuitar={quitarAccion} />
                      ))}
                    </ul>
                  )}
                </Card>
              )
            })}
          </div>
        ))}
      </section>

      <NoteBox>
        La IA armó este plan con tus{' '}
        <a href="/iniciativas" className="text-amber-700 underline">iniciativas priorizadas</a>. Edita las acciones o
        cambia su día, quita las que no quieras, o déjalo tal cual. "Regenerar con IA" reemplaza todas las acciones de
        la semana.
      </NoteBox>
    </>
  )
}
