import { useEffect, useState } from 'react'
import PageHeader from '../../components/ui/PageHeader'
import Card from '../../components/ui/Card'
import Button from '../../components/ui/Button'
import { UserBadge, AiBadge } from '../../components/ui/Badge'
import NoteBox from '../../components/ui/NoteBox'
import EmptyState from '../../components/ui/EmptyState'
import { inteligenciaApi } from '../../api/inteligencia'
import { ApiError } from '../../api/client'

export default function SmartPage() {
  const [metas, setMetas] = useState([])
  const [cargando, setCargando] = useState(true)
  const [generando, setGenerando] = useState(false)
  const [error, setError] = useState('')

  const cargar = () => {
    setCargando(true)
    inteligenciaApi
      .listarSmart()
      .then(setMetas)
      .catch((err) => setError(err instanceof ApiError ? err.message : 'No se pudieron cargar las metas SMART.'))
      .finally(() => setCargando(false))
  }

  useEffect(cargar, [])

  const generar = async () => {
    setError('')
    setGenerando(true)
    try {
      const nuevas = await inteligenciaApi.generarSmart()
      setMetas(nuevas)
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'No se pudieron generar las metas SMART.')
    } finally {
      setGenerando(false)
    }
  }

  return (
    <>
      <PageHeader
        title="Metas SMART · presencia digital"
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
      ) : metas.length === 0 ? (
        <Card>
          <EmptyState
            title="Todavía no has generado tus metas SMART"
            description="La IA propone 3 metas (constancia, presentación e interacción) a partir de tu negocio y tu FODA. La primera vez puede tardar hasta un minuto."
            action={
              <Button variant="success" loading={generando} onClick={generar}>
                {generando ? 'Generando… (puede tardar hasta 1 min)' : 'Generar metas con IA'}
              </Button>
            }
          />
        </Card>
      ) : (
        <>
          {metas.map((meta) => (
            <Card key={meta.id} className="text-sm">
              <h2 className="text-base font-semibold text-sky-700">{meta.titulo}</h2>
              <div className="grid grid-cols-1 lg:grid-cols-3 gap-3 mt-3">
                <div>
                  <p className="text-[11px] text-gray-500">Específico (di qué)</p>
                  <p>{meta.especifico}</p>
                </div>
                <div>
                  <p className="text-[11px] text-gray-500">Número meta (cuánto)</p>
                  <p>{meta.numeroMeta}</p>
                </div>
                <div>
                  <p className="text-[11px] text-gray-500">Fecha límite (cuándo)</p>
                  <p>{meta.fechaLimite}</p>
                </div>
                <div className="lg:col-span-2">
                  <p className="text-[11px] text-gray-500">Cómo medirás</p>
                  <p>{meta.medicion}</p>
                </div>
                <div>
                  <p className="text-[11px] text-gray-500">Pasos pequeños</p>
                  <p>{meta.pasos}</p>
                </div>
              </div>
            </Card>
          ))}

          <NoteBox variant="sky">
            ¿Tu negocio también vende o entrega en línea? Actívalo en{' '}
            <a href="/negocio" className="underline">Datos del negocio</a> — estas 3 metas son de presencia, no de ventas.
          </NoteBox>

          <div className="flex flex-wrap items-center justify-between gap-2">
            <Button to="/inteligencia/foda" variant="ghostSky">← Volver a FODA</Button>
            <div className="flex items-center gap-2">
              <Button variant="danger" loading={generando} onClick={generar}>
                {generando ? 'Generando…' : 'Generar de nuevo'}
              </Button>
              <Button to="/iniciativas" variant="success">Continuar → Iniciativas</Button>
            </div>
          </div>
        </>
      )}
    </>
  )
}
