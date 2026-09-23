import { useEffect, useState } from 'react'
import PageHeader from '../../components/ui/PageHeader'
import Card from '../../components/ui/Card'
import Button from '../../components/ui/Button'
import { UserBadge, AiBadge } from '../../components/ui/Badge'
import NoteBox from '../../components/ui/NoteBox'
import EmptyState from '../../components/ui/EmptyState'
import { inteligenciaApi } from '../../api/inteligencia'
import { ApiError } from '../../api/client'

const CUADRANTES = [
  { tipo: 'Fortaleza', titulo: 'Fortalezas', color: 'text-emerald-700' },
  { tipo: 'Oportunidad', titulo: 'Oportunidades', color: 'text-sky-700' },
  { tipo: 'Debilidad', titulo: 'Debilidades', color: 'text-amber-700' },
  { tipo: 'Amenaza', titulo: 'Amenazas', color: 'text-rose-700' },
]

export default function FodaPage() {
  const [items, setItems] = useState([])
  const [cargando, setCargando] = useState(true)
  const [generando, setGenerando] = useState(false)
  const [error, setError] = useState('')

  const cargar = () => {
    setCargando(true)
    inteligenciaApi
      .listarFoda()
      .then(setItems)
      .catch((err) => setError(err instanceof ApiError ? err.message : 'No se pudo cargar el FODA.'))
      .finally(() => setCargando(false))
  }

  useEffect(cargar, [])

  const generar = async () => {
    setError('')
    setGenerando(true)
    try {
      const nuevos = await inteligenciaApi.generarFoda()
      setItems(nuevos)
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'No se pudo generar el FODA.')
    } finally {
      setGenerando(false)
    }
  }

  return (
    <>
      <PageHeader
        title="FODA · ¿Qué tienes a favor y qué te frena en digital?"
        description={
          <>
            <UserBadge /> · <AiBadge className="ml-1" />
          </>
        }
      />

      {error && (
        <p className="text-xs text-rose-700 bg-rose-50 border border-rose-200 rounded-lg px-3 py-2">
          {error}
          {error.includes('clave de IA') && (
            <>
              {' '}
              <a href="/configuracion" className="underline font-medium">Ir a Configuración →</a>
            </>
          )}
        </p>
      )}

      {cargando ? (
        <p className="text-sm text-gray-500">Cargando…</p>
      ) : items.length === 0 ? (
        <Card>
          <EmptyState
            title="Todavía no has generado tu FODA"
            description="La IA lo arma a partir de los Datos del negocio que ya guardaste. La primera vez puede tardar hasta un minuto."
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
                      <li key={i.id} className="bg-gray-50 border border-gray-200 rounded-lg p-2 text-gray-800">
                        {i.contenido}
                      </li>
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
            <strong>Nota:</strong> Este análisis FODA fue <span className="text-emerald-700">generado con IA</span> en
            base a los <span className="text-sky-700">datos del negocio</span>. Si nada cambió desde la última vez,
            "Generar de nuevo" no vuelve a gastar una llamada real — te devuelve lo mismo al instante.
          </NoteBox>
        </>
      )}
    </>
  )
}
