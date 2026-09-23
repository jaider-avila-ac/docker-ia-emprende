import { useEffect, useState } from 'react'
import PageHeader from '../../components/ui/PageHeader'
import Card from '../../components/ui/Card'
import Button from '../../components/ui/Button'
import { AiBadge, UserBadge } from '../../components/ui/Badge'
import NoteBox from '../../components/ui/NoteBox'
import DataTable, { Td } from '../../components/ui/DataTable'
import EmptyState from '../../components/ui/EmptyState'
import { Field, Input, Select } from '../../components/ui/Field'
import { validateRequired } from '../../utils/validation'
import { ESTADO_COLOR } from './data'
import { iniciativasApi } from '../../api/iniciativas'
import { ApiError } from '../../api/client'

const emptyForm = { titulo: '', metaTipo: 'Constancia', estado: 'Propuesta', impacto: '3', confianza: '3', esfuerzo: '3', descripcion: '' }

export default function IniciativasPage() {
  const [iniciativas, setIniciativas] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState('')
  const [form, setForm] = useState(emptyForm)
  const [errors, setErrors] = useState({})
  const [guardando, setGuardando] = useState(false)

  const cargar = () => {
    setCargando(true)
    iniciativasApi
      .listar()
      .then(setIniciativas)
      .catch((err) => setError(err instanceof ApiError ? err.message : 'No se pudieron cargar las iniciativas.'))
      .finally(() => setCargando(false))
  }

  useEffect(cargar, [])

  const eliminar = async (id) => {
    setError('')
    try {
      await iniciativasApi.eliminar(id)
      setIniciativas((prev) => prev.filter((i) => i.id !== id))
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'No se pudo eliminar la iniciativa.')
    }
  }

  const crear = async (e) => {
    e.preventDefault()
    setError('')
    const nextErrors = validateRequired(form, ['titulo'])
    for (const campo of ['impacto', 'confianza', 'esfuerzo']) {
      const n = Number(form[campo])
      if (!Number.isInteger(n) || n < 1 || n > 5) nextErrors[campo] = '1 a 5.'
    }
    setErrors(nextErrors)
    if (Object.keys(nextErrors).length > 0) return

    setGuardando(true)
    try {
      await iniciativasApi.crear(form)
      setForm(emptyForm)
      cargar()
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'No se pudo crear la iniciativa.')
    } finally {
      setGuardando(false)
    }
  }

  return (
    <>
      <PageHeader
        title="Iniciativas · lista priorizada (ICE)"
        description={
          <>
            <AiBadge /> · <UserBadge className="ml-1" />
          </>
        }
      >
        <Button href="#crear" variant="warning">Nueva iniciativa</Button>
        <Button to="/plan" variant="ghostSky">Ir al plan semanal</Button>
      </PageHeader>

      {error && <p className="text-xs text-rose-700 bg-rose-50 border border-rose-200 rounded-lg px-3 py-2">{error}</p>}

      <Card as="section" id="crear">
        <h2 className="text-base font-semibold">Crear iniciativa</h2>
        <form className="grid grid-cols-1 lg:grid-cols-6 gap-3 text-sm mt-3" onSubmit={crear} noValidate>
          <Field label="Título" className="lg:col-span-2" error={errors.titulo}>
            <Input
              focus="amber"
              invalid={Boolean(errors.titulo)}
              placeholder="Ej: Video corto mostrando la cocina"
              value={form.titulo}
              onChange={(e) => setForm({ ...form, titulo: e.target.value })}
            />
          </Field>
          <Field label="Meta tipo">
            <Select focus="sky" value={form.metaTipo} onChange={(e) => setForm({ ...form, metaTipo: e.target.value })}>
              <option>Constancia</option>
              <option>Presentación</option>
              <option>Interacción</option>
              <option>Alcance</option>
            </Select>
          </Field>
          <Field label="Estado">
            <Select focus="emerald" value={form.estado} onChange={(e) => setForm({ ...form, estado: e.target.value })}>
              <option>Propuesta</option>
              <option>En prueba</option>
              <option>Aprobada</option>
              <option>Descartada</option>
            </Select>
          </Field>
          <Field label="Impacto (1-5)" error={errors.impacto}>
            <Input invalid={Boolean(errors.impacto)} value={form.impacto} onChange={(e) => setForm({ ...form, impacto: e.target.value })} />
          </Field>
          <Field label="Confianza (1-5)" error={errors.confianza}>
            <Input invalid={Boolean(errors.confianza)} value={form.confianza} onChange={(e) => setForm({ ...form, confianza: e.target.value })} />
          </Field>
          <Field label="Esfuerzo (1-5)" error={errors.esfuerzo} className="lg:col-span-1">
            <Input invalid={Boolean(errors.esfuerzo)} value={form.esfuerzo} onChange={(e) => setForm({ ...form, esfuerzo: e.target.value })} />
          </Field>
          <Field label="Explicación breve" className="lg:col-span-5">
            <Input
              focus="sky"
              placeholder="Ej: Mostrar el proceso genera más confianza que solo el resultado"
              value={form.descripcion}
              onChange={(e) => setForm({ ...form, descripcion: e.target.value })}
            />
          </Field>
          <div className="lg:col-span-1 flex items-end">
            <Button variant="success" type="submit" loading={guardando} className="w-full">
              {guardando ? 'Guardando…' : 'Guardar'}
            </Button>
          </div>
        </form>

        <NoteBox className="mt-3">
          <strong>Nota:</strong> Este formulario lo <span className="text-sky-700">completas tú</span>. La IA puede
          sugerir iniciativas a partir de tu FODA/SMART, pero la decisión final es tuya.
        </NoteBox>
      </Card>

      <Card>
        <div className="flex items-center justify-between">
          <h2 className="text-base font-semibold">Prioridad (orden por ICE)</h2>
          <div className="text-[11px] text-gray-500">Fórmula: (Impacto × Confianza) ÷ Esfuerzo</div>
        </div>

        <div className="mt-3">
          {cargando ? (
            <p className="text-sm text-gray-500">Cargando…</p>
          ) : iniciativas.length === 0 ? (
            <EmptyState
              title="Aún no hay iniciativas"
              description="Crea la primera arriba, o vuelve a Inteligencia para generar FODA/SMART primero."
              action={<Button href="#crear" variant="warning">Nueva iniciativa</Button>}
            />
          ) : (
            <DataTable columns={['Iniciativa', 'Meta', 'Estado', 'I/C/E', 'ICE', 'Acciones']}>
              {iniciativas.map((i) => (
                <tr key={i.id}>
                  <Td>
                    {i.titulo}
                    {i.generadaPorIa && <div className="text-[11px] text-gray-500">Generado por IA</div>}
                  </Td>
                  <Td>{i.metaTipo || '—'}</Td>
                  <Td className={ESTADO_COLOR[i.estado]}>{i.estado}</Td>
                  <Td>{i.impacto}/{i.confianza}/{i.esfuerzo}</Td>
                  <Td className="font-semibold">{i.ice.toFixed(1)}</Td>
                  <Td className="flex gap-1">
                    <Button to={`/iniciativas/${i.id}`} variant="primary" size="xs">Ver detalle</Button>
                    <Button variant="danger" size="xs" onClick={() => eliminar(i.id)}>Eliminar</Button>
                  </Td>
                </tr>
              ))}
            </DataTable>
          )}
        </div>

        <p className="text-[11px] text-gray-500 mt-2">
          Tip: empieza por "Aprobada/En prueba" con mayor ICE. Luego, envíalas al plan semanal.
        </p>
      </Card>
    </>
  )
}
