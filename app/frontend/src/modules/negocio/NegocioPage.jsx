import { useEffect, useState } from 'react'
import PageHeader from '../../components/ui/PageHeader'
import Card from '../../components/ui/Card'
import Button from '../../components/ui/Button'
import NoteBox from '../../components/ui/NoteBox'
import { Field, Input, Select, Textarea, Checkbox } from '../../components/ui/Field'
import NegocioNavCard from './components/NegocioNavCard'
import { validateRequired } from '../../utils/validation'
import { useBusiness } from '../../context/BusinessContext'
import { negociosApi } from '../../api/negocios'
import { ApiError } from '../../api/client'

const REDES = ['Instagram', 'Facebook', 'TikTok', 'WhatsApp Business']

const formDesde = (negocio) => ({
  nombre: negocio?.nombre || '',
  rubro: negocio?.rubro || '',
  ubicacion: negocio?.ubicacion || '',
  descripcion: negocio?.descripcion || '',
  publicoObjetivo: negocio?.publicoObjetivo || '',
  diferenciador: negocio?.diferenciador || '',
  vendeEnLinea: negocio?.vendeEnLinea || false,
  coberturaEnvio: negocio?.coberturaEnvio || '',
  redesActivas: negocio?.redesActivas || [],
})

export default function NegocioPage() {
  const { business, estado, crearNegocio, actualizarNegocio } = useBusiness()
  const [rubros, setRubros] = useState([])
  const [form, setForm] = useState(() => formDesde(business))
  const [errors, setErrors] = useState({})
  const [saving, setSaving] = useState(false)
  const [saved, setSaved] = useState(false)
  const [errorGeneral, setErrorGeneral] = useState('')

  useEffect(() => {
    setForm(formDesde(business))
  }, [business])

  useEffect(() => {
    negociosApi
      .rubrosDisponibles()
      .then(setRubros)
      .catch(() => setRubros([]))
  }, [])

  const onChange = (key) => (e) => setForm((prev) => ({ ...prev, [key]: e.target.value }))

  const toggleRed = (red) =>
    setForm((prev) => ({
      ...prev,
      redesActivas: prev.redesActivas.includes(red)
        ? prev.redesActivas.filter((r) => r !== red)
        : [...prev.redesActivas, red],
    }))

  const onSubmit = async (e) => {
    e.preventDefault()
    setErrorGeneral('')
    setSaved(false)

    const nextErrors = validateRequired(form, ['nombre', 'rubro'])
    setErrors(nextErrors)
    if (Object.keys(nextErrors).length > 0) return

    setSaving(true)
    try {
      if (business) {

        await actualizarNegocio({ ...business, ...form })
      } else {
        await crearNegocio(form)
      }
      setSaved(true)
    } catch (err) {
      setErrorGeneral(err instanceof ApiError ? err.message : 'No se pudo guardar el negocio.')
    } finally {
      setSaving(false)
    }
  }

  if (estado === 'cargando') {
    return <p className="text-sm text-gray-500">Cargando…</p>
  }

  return (
    <>
      <PageHeader
        title="Datos del negocio"
        description="La base para que la IA entienda tu negocio y te ayude con tu presencia digital."
      />

      {!business && (
        <NoteBox variant="amber">
          Todavía no has creado tu negocio — completa el formulario y guarda para crear el primero.
        </NoteBox>
      )}

      <form className="grid grid-cols-1 lg:grid-cols-2 gap-4 text-sm" onSubmit={onSubmit} noValidate>
        {errorGeneral && (
          <p className="lg:col-span-2 text-xs text-rose-700 bg-rose-50 border border-rose-200 rounded-lg px-3 py-2">
            {errorGeneral}
          </p>
        )}

        <Card className="space-y-3">
          <Field label="¿Cómo se llama tu negocio?" error={errors.nombre}>
            <Input invalid={Boolean(errors.nombre)} value={form.nombre} onChange={onChange('nombre')} />
          </Field>
          <Field label="¿A qué se dedica? (rubro)" error={errors.rubro}>
            <Select invalid={Boolean(errors.rubro)} value={form.rubro} onChange={onChange('rubro')}>
              <option value="">Selecciona…</option>
              {rubros.map((r) => (
                <option key={r}>{r}</option>
              ))}
            </Select>
          </Field>
          <Field label="¿Dónde está ubicado tu negocio?">
            <Input value={form.ubicacion} onChange={onChange('ubicacion')} placeholder="Ciudad y barrio/zona" />
          </Field>
          <Field label="¿Cómo describirías tu negocio en una frase?">
            <Textarea rows={2} value={form.descripcion} onChange={onChange('descripcion')} />
          </Field>
        </Card>

        <Card className="space-y-3">
          <Field label="¿A quién te gustaría llegar?">
            <Textarea rows={2} value={form.publicoObjetivo} onChange={onChange('publicoObjetivo')} />
          </Field>
          <Field label="¿Qué te diferencia hoy?">
            <Textarea rows={2} value={form.diferenciador} onChange={onChange('diferenciador')} />
          </Field>

          <Checkbox
            label="También vendo o entrego por internet (domicilios, envíos)"
            checked={form.vendeEnLinea}
            onChange={(e) => setForm((prev) => ({ ...prev, vendeEnLinea: e.target.checked }))}
          />
          {form.vendeEnLinea && (
            <Field label="Cobertura de envío/domicilio">
              <Input
                value={form.coberturaEnvio}
                onChange={onChange('coberturaEnvio')}
                placeholder="Ej.: Domicilios en el barrio, 30 min"
              />
            </Field>
          )}

          <NoteBox variant="sky" className="mt-2!">
            Por defecto asumimos que quieres <strong>presencia digital</strong> (redes bien llevadas), no una tienda en
            línea. Marca la casilla de arriba solo si tu negocio también vende/entrega por internet.
          </NoteBox>
        </Card>

        <Card className="lg:col-span-2 space-y-2">
          <h2 className="text-sm font-semibold">¿Cómo están tus redes hoy?</h2>
          <p className="text-xs text-gray-500">Esto ayuda a saber si hay que empezar de cero o mejorar lo que ya tienes.</p>
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-2 text-xs pt-1">
            {REDES.map((red) => (
              <Checkbox key={red} label={red} checked={form.redesActivas.includes(red)} onChange={() => toggleRed(red)} />
            ))}
          </div>
        </Card>

        <div className="lg:col-span-2 flex items-center gap-2">
          <Button variant="success" type="submit" loading={saving}>
            {saving ? 'Guardando…' : business ? 'Guardar' : 'Crear negocio'}
          </Button>
          {business && <Button to="/inteligencia" variant="primary">Generar inteligencia (IA)</Button>}
          {saved && <span className="text-xs text-emerald-600">✓ Guardado</span>}
        </div>
      </form>

      {business && (
        <section className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-4">
          <NegocioNavCard to="/negocio/ofertas" color="emerald" title="Ofertas (prod/serv)" description="Lo que ofreces, para mencionarlo en tus publicaciones." />
          <NegocioNavCard to="/negocio/competidores" color="sky" title="Competidores" description="Qué hacen otros negocios parecidos en redes." />
          <NegocioNavCard to="/negocio/branding" color="amber" title="Branding" description="Cómo quieres presentarte y qué tono usar." />
          <NegocioNavCard to="/negocio/resultados" color="rose" title="Resultados" description="Mide si lo que publicaste realmente funcionó." />
        </section>
      )}

      <NoteBox>
        <strong>Nota:</strong> Esta sección <span className="text-emerald-700">NO es generada por IA</span>. Toda la
        información aquí la escribe el usuario y se usa como base para que la IA proponga FODA y metas.
      </NoteBox>
    </>
  )
}
