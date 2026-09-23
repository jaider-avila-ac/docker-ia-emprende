import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import Card from '../../components/ui/Card'
import Button from '../../components/ui/Button'
import NoteBox from '../../components/ui/NoteBox'
import EmptyState from '../../components/ui/EmptyState'
import { Input, Select } from '../../components/ui/Field'
import { planApi } from '../../api/plan'
import { ApiError } from '../../api/client'

const DIAS = ['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo']

export default function SemanaPage() {
  const { numero } = useParams()
  const [plan, setPlan] = useState(null)
  const [existe, setExiste] = useState(true)
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState('')
  const [nuevaDescripcion, setNuevaDescripcion] = useState('')
  const [nuevoDia, setNuevoDia] = useState('Lunes')
  const [agregando, setAgregando] = useState(false)

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
        else setError(err instanceof ApiError ? err.message : 'No se pudo cargar la semana.')
      })
      .finally(() => setCargando(false))
  }

  useEffect(cargar, [numero])

  const agregarAccion = async (e) => {
    e.preventDefault()
    if (!nuevaDescripcion.trim()) return
    setAgregando(true)
    try {
      await planApi.agregarAccion(numero, { diaSemana: nuevoDia, descripcion: nuevaDescripcion.trim() })
      setNuevaDescripcion('')
      cargar()
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'No se pudo agregar la acción.')
    } finally {
      setAgregando(false)
    }
  }

  const eliminarAccion = async (id) => {
    try {
      await planApi.eliminarAccion(id)
      cargar()
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'No se pudo eliminar la acción.')
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
            description="Ve al plan y ajusta la cadencia para crear esta semana, o agrega una acción abajo para crearla ahora mismo."
          />
        </Card>
        <Card className="mt-3">
          <form onSubmit={agregarAccion} className="flex flex-wrap items-end gap-2 text-sm">
            <Select value={nuevoDia} onChange={(e) => setNuevoDia(e.target.value)}>
              {DIAS.map((d) => <option key={d}>{d}</option>)}
            </Select>
            <Input
              className="flex-1 min-w-[200px]"
              placeholder="Ej.: Post con el menú del día"
              value={nuevaDescripcion}
              onChange={(e) => setNuevaDescripcion(e.target.value)}
            />
            <Button variant="success" type="submit" loading={agregando}>Agregar y crear semana</Button>
          </form>
        </Card>
      </>
    )
  }

  return (
    <>
      <header className="flex items-center justify-between">
        <div>
          <Link to="/plan" className="text-xs text-sky-700 hover:underline">← Volver al plan</Link>
          <h1 className="text-lg font-semibold mt-1">Semana {plan.semanaNumero} · {plan.anio}</h1>
          <p className="text-xs text-gray-500">Ventana recomendada: {plan.ventanaHoraria || '—'}</p>
        </div>
        <div className="flex gap-2">
          <Button to="/iniciativas" variant="warning">Ir a Iniciativas</Button>
          <Button to="/evaluacion" variant="success">Evaluar semana</Button>
        </div>
      </header>

      {error && <p className="text-xs text-rose-700 bg-rose-50 border border-rose-200 rounded-lg px-3 py-2">{error}</p>}

      <Card>
        <form onSubmit={agregarAccion} className="flex flex-wrap items-end gap-2 text-sm">
          <Select value={nuevoDia} onChange={(e) => setNuevoDia(e.target.value)}>
            {DIAS.map((d) => <option key={d}>{d}</option>)}
          </Select>
          <Input
            className="flex-1 min-w-[200px]"
            placeholder="Ej.: Post con el menú del día"
            value={nuevaDescripcion}
            onChange={(e) => setNuevaDescripcion(e.target.value)}
          />
          <Button variant="success" type="submit" loading={agregando}>Agregar acción</Button>
        </form>
      </Card>

      <section className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        {[DIAS.slice(0, 4), DIAS.slice(4)].map((columna, i) => (
          <div key={i} className="space-y-4">
            {columna.map((dia) => {
              const acciones = plan.acciones.filter((a) => a.diaSemana === dia)
              return (
                <Card key={dia} as="article" className="text-sm">
                  <h2 className="text-base font-semibold">{dia}</h2>
                  {acciones.length === 0 ? (
                    <p className="text-gray-400 mt-2 text-xs">Sin acciones todavía.</p>
                  ) : (
                    <ul className="mt-2 text-gray-700 space-y-1">
                      {acciones.map((a) => (
                        <li key={a.id} className="flex items-center justify-between gap-2">
                          <span>• {a.descripcion}</span>
                          <button
                            type="button"
                            onClick={() => eliminarAccion(a.id)}
                            className="text-[11px] text-rose-600 hover:underline shrink-0"
                          >
                            Eliminar
                          </button>
                        </li>
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
        <strong>Nota:</strong> El plan semanal se arma a partir de las{' '}
        <a href="/iniciativas" className="text-amber-700 underline">iniciativas priorizadas</a>. Ajusta el plan según
        disponibilidad y resultados.
      </NoteBox>
    </>
  )
}
