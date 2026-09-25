import { useEffect, useState } from 'react'
import PageHeader from '../../components/ui/PageHeader'
import Card from '../../components/ui/Card'
import Button from '../../components/ui/Button'
import { AiBadge } from '../../components/ui/Badge'
import NoteBox from '../../components/ui/NoteBox'
import EmptyState from '../../components/ui/EmptyState'
import ErrorIa from '../../components/ui/ErrorIa'
import { Textarea } from '../../components/ui/Field'
import { inteligenciaApi } from '../../api/inteligencia'
import { ApiError } from '../../api/client'

const CUADRANTES = [
  { tipo: 'Fortaleza', titulo: 'Fortalezas', color: 'text-emerald-700' },
  { tipo: 'Oportunidad', titulo: 'Oportunidades', color: 'text-sky-700' },
  { tipo: 'Debilidad', titulo: 'Debilidades', color: 'text-amber-700' },
  { tipo: 'Amenaza', titulo: 'Amenazas', color: 'text-rose-700' },
]

function ItemFoda({ item, onGuardar, onQuitar }) {
  const [editando, setEditando] = useState(false)
  const [texto, setTexto] = useState(item.contenido)
  const [guardando, setGuardando] = useState(false)

  const guardar = async () => {
    if (!texto.trim()) return
    setGuardando(true)
    const ok = await onGuardar(item.id, texto.trim())
    setGuardando(false)
    if (ok) setEditando(false)
  }

  if (editando) {
    return (
      <li className="bg-white border border-sky-300 rounded-lg p-2 space-y-2">
        <Textarea focus="sky" rows={3} value={texto} onChange={(e) => setTexto(e.target.value)} />
        <div className="flex gap-2">
          <Button variant="success" size="xs" loading={guardando} onClick={guardar}>Guardar</Button>
          <Button
            variant="subtle"
            size="xs"
            onClick={() => {
              setTexto(item.contenido)
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
    <li className="bg-gray-50 border border-gray-200 rounded-lg p-2 text-gray-800">
      <p>{item.contenido}</p>
      <div className="flex gap-3 mt-1">
        <button type="button" onClick={() => setEditando(true)} className="text-[11px] text-sky-700 hover:underline">
          Editar
        </button>
        <button type="button" onClick={() => onQuitar(item.id)} className="text-[11px] text-rose-600 hover:underline">
          Quitar
        </button>
      </div>
    </li>
  )
}

export default function FodaPage() {
  const [items, setItems] = useState([])
  const [cargando, setCargando] = useState(true)
  const [generando, setGenerando] = useState(false)
  const [error, setError] = useState('')

  const mensajeDe = (err, porDefecto) => (err instanceof ApiError ? err.message : porDefecto)

  useEffect(() => {
    inteligenciaApi
      .listarFoda()
      .then(setItems)
      .catch((err) => setError(mensajeDe(err, 'No se pudo cargar el FODA.')))
      .finally(() => setCargando(false))
  }, [])

  const generar = async () => {
    setError('')
    setGenerando(true)
    try {
      setItems(await inteligenciaApi.generarFoda())
    } catch (err) {
      setError(mensajeDe(err, 'No se pudo generar el FODA.'))
    } finally {
      setGenerando(false)
    }
  }

  const guardar = async (id, contenido) => {
    setError('')
    try {
      const actualizado = await inteligenciaApi.actualizarFoda(id, contenido)
      setItems((prev) => prev.map((i) => (i.id === id ? actualizado : i)))
      return true
    } catch (err) {
      setError(mensajeDe(err, 'No se pudo guardar el cambio.'))
      return false
    }
  }

  const quitar = async (id) => {
    setError('')
    try {
      await inteligenciaApi.eliminarFoda(id)
      setItems((prev) => prev.filter((i) => i.id !== id))
    } catch (err) {
      setError(mensajeDe(err, 'No se pudo quitar el elemento.'))
    }
  }

  return (
    <>
      <PageHeader
        title="FODA · ¿Qué tienes a favor y qué te frena en digital?"
        description={<AiBadge />}
      />

      <ErrorIa mensaje={error} />

      {cargando ? (
        <p className="text-sm text-gray-500">Cargando…</p>
      ) : items.length === 0 ? (
        <Card>
          <EmptyState
            title="Todavía no has generado tu FODA"
            description="La IA lo arma a partir de los datos de tu negocio, tus ofertas, competidores y branding. Después puedes editar lo que quieras. La primera vez puede tardar hasta un minuto."
            action={
              <Button variant="success" loading={generando} onClick={generar}>
                {generando ? 'Generando… (puede tardar hasta 1 min)' : 'Generar FODA con IA'}
              </Button>
            }
          />
        </Card>
      ) : (
        <>
          <section className="grid grid-cols-1 lg:grid-cols-2 gap-4 text-sm">
            {CUADRANTES.map((c) => (
              <Card key={c.tipo}>
                <h2 className={`text-base font-semibold ${c.color}`}>{c.titulo}</h2>
                <ul className="mt-2 space-y-2">
                  {items
                    .filter((i) => i.tipo === c.tipo)
                    .map((i) => (
                      <ItemFoda key={i.id} item={i} onGuardar={guardar} onQuitar={quitar} />
                    ))}
                </ul>
              </Card>
            ))}
          </section>

          <div className="flex flex-wrap items-center justify-between gap-2">
            <Button to="/inteligencia" variant="ghostSky">← Volver a Inteligencia</Button>
            <div className="flex items-center gap-2">
              <Button variant="danger" loading={generando} onClick={generar}>
                {generando ? 'Generando…' : 'Generar de nuevo'}
              </Button>
              <Button to="/inteligencia/smart" variant="success">Continuar → SMART</Button>
            </div>
          </div>

          <NoteBox>
            La IA generó este análisis. Edita o quita lo que no encaje contigo, o déjalo tal cual. Ojo: si pulsas
            "Generar de nuevo" y tus datos cambiaron, se reemplaza todo, incluidas tus ediciones.
          </NoteBox>
        </>
      )}
    </>
  )
}
