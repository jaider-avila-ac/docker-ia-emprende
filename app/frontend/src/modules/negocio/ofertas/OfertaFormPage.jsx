import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import PageHeader from '../../../components/ui/PageHeader'
import Button from '../../../components/ui/Button'
import NoteBox from '../../../components/ui/NoteBox'
import { Field, Input, Select, Textarea } from '../../../components/ui/Field'
import { useSavedFeedback } from '../../../hooks/useSavedFeedback'
import { validateRequired } from '../../../utils/validation'
import { ofertasApi } from '../../../api/ofertas'
import { ApiError } from '../../../api/client'

const emptyForm = { tipo: 'Producto', nombre: '', categoria: '', precio: '', destacar: '' }

export default function OfertaFormPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const isEdit = Boolean(id)

  const [form, setForm] = useState(emptyForm)
  const [errors, setErrors] = useState({})
  const [errorGeneral, setErrorGeneral] = useState('')
  const [cargando, setCargando] = useState(isEdit)
  const { saving, saved, run } = useSavedFeedback()
  const onChange = (key) => (e) => setForm((prev) => ({ ...prev, [key]: e.target.value }))

  useEffect(() => {
    if (!isEdit) return
    ofertasApi
      .listar()
      .then((ofertas) => {
        const found = ofertas.find((o) => String(o.id) === id)
        if (found) setForm({ ...emptyForm, ...found, precio: found.precio ?? '' })
        else setErrorGeneral('Esta oferta ya no existe.')
      })
      .catch(() => setErrorGeneral('No se pudo cargar la oferta.'))
      .finally(() => setCargando(false))
  }, [id, isEdit])

  const onSubmit = async (e) => {
    e.preventDefault()
    setErrorGeneral('')

    const nextErrors = validateRequired(form, ['nombre'])
    if (form.precio !== '' && Number.isNaN(Number(form.precio))) {
      nextErrors.precio = 'Debe ser un número.'
    }
    setErrors(nextErrors)
    if (Object.keys(nextErrors).length > 0) return

    try {
      await run(() => (isEdit ? ofertasApi.actualizar(id, form) : ofertasApi.crear(form)))
    } catch (err) {
      setErrorGeneral(err instanceof ApiError ? err.message : 'No se pudo guardar la oferta.')
    }
  }

  const onEliminar = async () => {
    try {
      await ofertasApi.eliminar(id)
      navigate('/negocio/ofertas')
    } catch (err) {
      setErrorGeneral(err instanceof ApiError ? err.message : 'No se pudo eliminar la oferta.')
    }
  }

  if (cargando) return <p className="text-sm text-gray-500">Cargando…</p>

  return (
    <>
      <PageHeader title={isEdit ? `Editar oferta: ${form.nombre}` : 'Nueva oferta'}>
        <Button to="/negocio/ofertas" variant="neutral">← Volver a ofertas</Button>
        <Button to="/negocio" variant="neutral">Datos del negocio</Button>
      </PageHeader>

      {errorGeneral && (
        <p className="text-xs text-rose-700 bg-rose-50 border border-rose-200 rounded-lg px-3 py-2">{errorGeneral}</p>
      )}

      <form
        className="bg-white border border-gray-200 rounded-2xl p-4 grid grid-cols-1 lg:grid-cols-3 gap-3 text-sm"
        onSubmit={onSubmit}
        noValidate
      >
        <Field label="Tipo">
          <Select value={form.tipo} onChange={onChange('tipo')}>
            <option>Producto</option>
            <option>Servicio</option>
          </Select>
        </Field>
        <Field label="Nombre" error={errors.nombre}>
          <Input invalid={Boolean(errors.nombre)} value={form.nombre} onChange={onChange('nombre')} placeholder="Ej.: Sancocho de gallina" />
        </Field>
        <Field label="Categoría">
          <Input value={form.categoria} onChange={onChange('categoria')} placeholder="Ej.: Plato del día" />
        </Field>
        <Field label="Precio (opcional, para mencionarlo)" error={errors.precio}>
          <Input invalid={Boolean(errors.precio)} value={form.precio} onChange={onChange('precio')} placeholder="$" />
        </Field>
        <Field label="¿Qué lo hace especial?" className="lg:col-span-2">
          <Textarea
            value={form.destacar}
            onChange={onChange('destacar')}
            placeholder="Ej.: Receta de la casa, se ve muy bien en foto recién servido…"
          />
        </Field>

        <div className="lg:col-span-3 flex flex-wrap items-center gap-2 pt-1">
          <Button variant="success" type="submit" loading={saving}>
            {saving ? 'Guardando…' : isEdit ? 'Actualizar' : 'Guardar'}
          </Button>
          <Button to="/negocio/ofertas" variant="primary">Cancelar</Button>
          {isEdit && <Button variant="danger" type="button" onClick={onEliminar}>Eliminar</Button>}
          {saved && <span className="text-xs text-emerald-600">✓ Guardado</span>}
        </div>

        <NoteBox className="lg:col-span-3 mt-1">
          <strong>Nota:</strong> Esta información la mantiene el <span className="text-sky-700 font-medium">usuario</span>.
          La IA no la inventa; la usa para saber qué mostrar y qué destacar en tus publicaciones.
        </NoteBox>
      </form>
    </>
  )
}
