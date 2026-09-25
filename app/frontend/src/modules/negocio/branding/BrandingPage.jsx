import { useEffect, useState } from 'react'
import PageHeader from '../../../components/ui/PageHeader'
import Card from '../../../components/ui/Card'
import Button from '../../../components/ui/Button'
import { AiBadge } from '../../../components/ui/Badge'
import ErrorIa from '../../../components/ui/ErrorIa'
import NoteBox from '../../../components/ui/NoteBox'
import { Input, Checkbox } from '../../../components/ui/Field'
import { useSavedFeedback } from '../../../hooks/useSavedFeedback'
import { useBusiness } from '../../../context/BusinessContext'
import { inteligenciaApi } from '../../../api/inteligencia'
import { ApiError } from '../../../api/client'

const PILARES = ['Educativo', 'Oferta', 'Prueba social', 'Interacción', 'Servicio']

const formDesde = (negocio) => ({
  tagline: negocio?.tagline || '',
  tono: negocio?.tono || '',
  colores: negocio?.colores || '',
  referenciasEstilo: negocio?.referenciasEstilo || '',
  pilares: negocio?.pilares || [],
})

export default function BrandingPage() {
  const { business, actualizarNegocio } = useBusiness()
  const [form, setForm] = useState(() => formDesde(business))
  const [errorGeneral, setErrorGeneral] = useState('')
  const { saving, saved, run } = useSavedFeedback()
  const [sugiriendo, setSugiriendo] = useState(false)
  const [sugerido, setSugerido] = useState(false)

  useEffect(() => setForm(formDesde(business)), [business])

  const togglePilar = (pilar) =>
    setForm((prev) => ({
      ...prev,
      pilares: prev.pilares.includes(pilar) ? prev.pilares.filter((p) => p !== pilar) : [...prev.pilares, pilar],
    }))

  const sugerir = async () => {
    setErrorGeneral('')
    setSugiriendo(true)
    try {
      const s = await inteligenciaApi.sugerirBranding()
      setForm({
        tagline: s.tagline || '',
        tono: s.tono || '',
        colores: s.colores || '',
        referenciasEstilo: s.referenciasEstilo || '',
        pilares: s.pilares || [],
      })
      setSugerido(true)
    } catch (err) {
      setErrorGeneral(err instanceof ApiError ? err.message : 'No se pudo generar la sugerencia.')
    } finally {
      setSugiriendo(false)
    }
  }

  const onSubmit = async (e) => {
    e.preventDefault()
    setErrorGeneral('')
    try {
      await run(() => actualizarNegocio({ ...business, ...form }))
    } catch (err) {
      setErrorGeneral(err instanceof ApiError ? err.message : 'No se pudo guardar el branding.')
    }
  }

  if (!business) {
    return (
      <NoteBox variant="amber">
        Primero crea tu negocio en <a href="/negocio" className="underline">Datos del negocio</a> para poder definir su branding.
      </NoteBox>
    )
  }

  return (
    <>
      <PageHeader
        title="Branding"
        description={<AiBadge />}
      >
        <Button variant="success" loading={sugiriendo} onClick={sugerir}>
          {sugiriendo ? 'Pensando…' : 'Sugerir con IA'}
        </Button>
      </PageHeader>

      <ErrorIa mensaje={errorGeneral} />

      {sugerido && (
        <NoteBox variant="sky">
          La IA llenó el formulario con su propuesta. Cámbiala si quieres y pulsa Guardar; todavía no se ha guardado.
        </NoteBox>
      )}

      <Card as="form" onSubmit={onSubmit} className="space-y-3 text-sm max-w-2xl">
        <label>
          ¿Qué frase corta te representa (tagline)?
          <Input className="mt-1" value={form.tagline} onChange={(e) => setForm({ ...form, tagline: e.target.value })} />
        </label>
        <label>
          ¿Cómo quieres sonar (tono)?
          <Input className="mt-1" value={form.tono} onChange={(e) => setForm({ ...form, tono: e.target.value })} />
        </label>
        <label>
          ¿Qué colores usas o te gustan?
          <Input className="mt-1" value={form.colores} onChange={(e) => setForm({ ...form, colores: e.target.value })} />
        </label>
        <label>
          ¿Alguna cuenta o negocio que te guste cómo se ve? (opcional)
          <Input
            className="mt-1"
            placeholder="Ej.: @tal_restaurante, me gusta cómo muestran su cocina"
            value={form.referenciasEstilo}
            onChange={(e) => setForm({ ...form, referenciasEstilo: e.target.value })}
          />
        </label>

        <fieldset className="mt-2">
          <legend className="text-sm font-semibold mb-1">¿Qué pilares te describen?</legend>
          <div className="grid grid-cols-2 gap-2 text-xs">
            {PILARES.map((p) => (
              <Checkbox key={p} label={p} checked={form.pilares.includes(p)} onChange={() => togglePilar(p)} />
            ))}
          </div>
        </fieldset>

        <div className="flex items-center gap-2 pt-2">
          <Button variant="success" type="submit" loading={saving}>{saving ? 'Guardando…' : 'Guardar'}</Button>
          <Button variant="neutral" type="button" onClick={() => setForm(formDesde(business))}>Restablecer</Button>
          {saved && <span className="text-xs text-emerald-600">✓ Guardado</span>}
        </div>
      </Card>

      <NoteBox>
        Esto le da el tono a todo lo que la IA genera para ti en{' '}
        <a href="/inteligencia" className="underline">Inteligencia</a>. Pide una propuesta con "Sugerir con IA" y
        ajústala, o déjala tal cual.
      </NoteBox>
    </>
  )
}
