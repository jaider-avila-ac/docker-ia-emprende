import { useEffect, useState } from 'react'
import PageHeader from '../../components/ui/PageHeader'
import Card from '../../components/ui/Card'
import Button from '../../components/ui/Button'
import { AiBadge } from '../../components/ui/Badge'
import NoteBox from '../../components/ui/NoteBox'
import EmptyState from '../../components/ui/EmptyState'
import ErrorIa from '../../components/ui/ErrorIa'
import { Field, Input, Textarea } from '../../components/ui/Field'
import { inteligenciaApi } from '../../api/inteligencia'
import { ApiError } from '../../api/client'

function MetaSmart({ meta, onGuardar, onQuitar }) {
  const [editando, setEditando] = useState(false)
  const [form, setForm] = useState(meta)
  const [guardando, setGuardando] = useState(false)

  const cambiar = (campo) => (e) => setForm({ ...form, [campo]: e.target.value })

  const guardar = async (e) => {
    e.preventDefault()
    if (!form.titulo?.trim()) return
    setGuardando(true)
    const ok = await onGuardar(meta.id, form)
    setGuardando(false)
    if (ok) setEditando(false)
  }

  if (editando) {
    return (
      <Card as="form" onSubmit={guardar} className="text-sm space-y-3">
        <Field label="Título">
          <Input focus="sky" value={form.titulo || ''} onChange={cambiar('titulo')} />
        </Field>
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-3">
          <Field label="Específico (di qué)" className="lg:col-span-3">
            <Textarea focus="sky" rows={2} value={form.especifico || ''} onChange={cambiar('especifico')} />
          </Field>
          <Field label="Número meta (cuánto)">
            <Input focus="sky" value={form.numeroMeta || ''} onChange={cambiar('numeroMeta')} />
          </Field>
          <Field label="Fecha límite (cuándo)">
            <Input focus="sky" type="date" value={form.fechaLimite || ''} onChange={cambiar('fechaLimite')} />
          </Field>
          <Field label="Cómo medirás" className="lg:col-span-3">
            <Textarea focus="sky" rows={2} value={form.medicion || ''} onChange={cambiar('medicion')} />
          </Field>
          <Field label="Pasos pequeños" className="lg:col-span-3">
            <Textarea focus="sky" rows={2} value={form.pasos || ''} onChange={cambiar('pasos')} />
          </Field>
        </div>
        <div className="flex gap-2">
          <Button variant="success" type="submit" loading={guardando}>Guardar</Button>
          <Button
            variant="subtle"
            type="button"
            onClick={() => {
              setForm(meta)
              setEditando(false)
            }}
          >
            Cancelar
          </Button>
        </div>
      </Card>
    )
  }

  return (
    <Card className="text-sm">
      <div className="flex items-start justify-between gap-3">
        <h2 className="text-base font-semibold text-sky-700">{meta.titulo}</h2>
        <div className="flex gap-3 shrink-0">
          <button type="button" onClick={() => setEditando(true)} className="text-[11px] text-sky-700 hover:underline">
            Editar
          </button>
          <button type="button" onClick={() => onQuitar(meta.id)} className="text-[11px] text-rose-600 hover:underline">
            Quitar
          </button>
        </div>
      </div>
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
  )
}

export default function SmartPage() {
  const [metas, setMetas] = useState([])
  const [cargando, setCargando] = useState(true)
  const [generando, setGenerando] = useState(false)
  const [error, setError] = useState('')

  const mensajeDe = (err, porDefecto) => (err instanceof ApiError ? err.message : porDefecto)

  useEffect(() => {
    inteligenciaApi
      .listarSmart()
      .then(setMetas)
      .catch((err) => setError(mensajeDe(err, 'No se pudieron cargar las metas SMART.')))
      .finally(() => setCargando(false))
  }, [])

  const generar = async () => {
    setError('')
    setGenerando(true)
    try {
      setMetas(await inteligenciaApi.generarSmart())
    } catch (err) {
      setError(mensajeDe(err, 'No se pudieron generar las metas SMART.'))
    } finally {
      setGenerando(false)
    }
  }

  const guardar = async (id, meta) => {
    setError('')
    try {
      const actualizada = await inteligenciaApi.actualizarSmart(id, meta)
      setMetas((prev) => prev.map((m) => (m.id === id ? actualizada : m)))
      return true
    } catch (err) {
      setError(mensajeDe(err, 'No se pudo guardar el cambio.'))
      return false
    }
  }

  const quitar = async (id) => {
    setError('')
    try {
      await inteligenciaApi.eliminarSmart(id)
      setMetas((prev) => prev.filter((m) => m.id !== id))
    } catch (err) {
      setError(mensajeDe(err, 'No se pudo quitar la meta.'))
    }
  }

  return (
    <>
      <PageHeader title="Metas SMART · presencia digital" description={<AiBadge />} />

      <ErrorIa mensaje={error} />

      {cargando ? (
        <p className="text-sm text-gray-500">Cargando…</p>
      ) : metas.length === 0 ? (
        <Card>
          <EmptyState
            title="Todavía no has generado tus metas SMART"
            description="La IA propone 3 metas (constancia, presentación e interacción) a partir de tu negocio. Después puedes editarlas. La primera vez puede tardar hasta un minuto."
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
            <MetaSmart key={meta.id} meta={meta} onGuardar={guardar} onQuitar={quitar} />
          ))}

          <NoteBox variant="sky">
            La IA generó estas metas. Edítalas o déjalas tal cual. Si tu negocio también vende en línea, actívalo en{' '}
            <a href="/negocio" className="underline">Datos del negocio</a>: estas 3 metas son de presencia, no de ventas.
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
