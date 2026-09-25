import { useEffect, useState } from 'react'
import PageHeader from '../../components/ui/PageHeader'
import Card from '../../components/ui/Card'
import Button from '../../components/ui/Button'
import { AiBadge } from '../../components/ui/Badge'
import ErrorIa from '../../components/ui/ErrorIa'
import NoteBox from '../../components/ui/NoteBox'
import DataTable, { Td } from '../../components/ui/DataTable'
import EmptyState from '../../components/ui/EmptyState'
import { Field, Select, Textarea, Input } from '../../components/ui/Field'
import { iniciativasApi } from '../../api/iniciativas'
import { evaluacionesApi } from '../../api/evaluaciones'
import { inteligenciaApi } from '../../api/inteligencia'
import { ApiError } from '../../api/client'

const emptyForm = { iniciativaId: '', semanaNumero: '', seLogro: 'Sí', dificultad: 'Baja', repetiria: 'Sí', comentarios: '' }

export default function EvaluacionPage() {
  const [iniciativas, setIniciativas] = useState([])
  const [evaluaciones, setEvaluaciones] = useState([])
  const [cargando, setCargando] = useState(true)
  const [enviando, setEnviando] = useState(false)
  const [error, setError] = useState('')
  const [form, setForm] = useState(emptyForm)
  const [analisis, setAnalisis] = useState(null)
  const [analizando, setAnalizando] = useState(false)

  const cargar = () => {
    setCargando(true)
    Promise.all([iniciativasApi.listar(), evaluacionesApi.listar()])
      .then(([iniciativas, evaluaciones]) => {
        setIniciativas(iniciativas)
        setEvaluaciones(evaluaciones)
        if (iniciativas.length > 0) {
          setForm((prev) => ({ ...prev, iniciativaId: prev.iniciativaId || String(iniciativas[0].id) }))
        }
      })
      .catch((err) => setError(err instanceof ApiError ? err.message : 'No se pudo cargar la información.'))
      .finally(() => setCargando(false))
  }

  useEffect(cargar, [])

  const enviar = async (e) => {
    e.preventDefault()
    if (!form.iniciativaId) {
      setError('Crea al menos una iniciativa antes de evaluarla.')
      return
    }
    setError('')
    setEnviando(true)
    try {
      await evaluacionesApi.crear(form)
      setForm((prev) => ({ ...emptyForm, iniciativaId: prev.iniciativaId }))
      cargar()
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'No se pudo enviar la evaluación.')
    } finally {
      setEnviando(false)
    }
  }

  const analizar = async () => {
    setError('')
    setAnalizando(true)
    try {
      setAnalisis(await inteligenciaApi.analizar())
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'No se pudo generar el análisis.')
    } finally {
      setAnalizando(false)
    }
  }

  const eliminar = async (id) => {
    try {
      await evaluacionesApi.eliminar(id)
      setEvaluaciones((prev) => prev.filter((e) => e.id !== id))
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'No se pudo eliminar la evaluación.')
    }
  }

  const tituloDe = (iniciativaId) => iniciativas.find((i) => i.id === iniciativaId)?.titulo || `#${iniciativaId}`

  if (cargando) return <p className="text-sm text-gray-500">Cargando…</p>

  return (
    <>
      <PageHeader
        title="Evaluación breve · para entrenar la IA"
        description={
          <>
            No hace falta contabilidad detallada; solo cómo te fue en general (los números van en{' '}
            <a href="/negocio/resultados" className="underline">Resultados</a>). <AiBadge className="ml-1" />
          </>
        }
      />

      <ErrorIa mensaje={error} />

      {iniciativas.length === 0 ? (
        <Card>
          <EmptyState
            title="Aún no tienes iniciativas para evaluar"
            description="Genera tus iniciativas con IA y pruébalas; después vuelve aquí a contar cómo te fue."
            action={<Button to="/iniciativas" variant="warning">Ir a Iniciativas</Button>}
          />
        </Card>
      ) : (
        <Card as="form" onSubmit={enviar} className="text-sm">
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-3">
            <Field label="Iniciativa">
              <Select focus="emerald" value={form.iniciativaId} onChange={(e) => setForm({ ...form, iniciativaId: e.target.value })}>
                {iniciativas.map((i) => (
                  <option key={i.id} value={i.id}>{i.titulo}</option>
                ))}
              </Select>
            </Field>
            <Field label="N.° de semana (opcional)">
              <Input placeholder="Ej.: 35" value={form.semanaNumero} onChange={(e) => setForm({ ...form, semanaNumero: e.target.value })} />
            </Field>
            <Field label="¿Se logró lo esperado?">
              <Select focus="amber" value={form.seLogro} onChange={(e) => setForm({ ...form, seLogro: e.target.value })}>
                <option>Sí</option>
                <option>Parcial</option>
                <option>No</option>
              </Select>
            </Field>
            <Field label="Dificultad">
              <Select focus="rose" value={form.dificultad} onChange={(e) => setForm({ ...form, dificultad: e.target.value })}>
                <option>Baja</option>
                <option>Media</option>
                <option>Alta</option>
              </Select>
            </Field>
            <Field label="¿Repetirías?">
              <Select focus="emerald" value={form.repetiria} onChange={(e) => setForm({ ...form, repetiria: e.target.value })}>
                <option>Sí</option>
                <option>Con cambios</option>
                <option>No</option>
              </Select>
            </Field>
            <Field label="Comentarios (¿qué mejorar?)" className="lg:col-span-3">
              <Textarea focus="sky" rows={3} value={form.comentarios} onChange={(e) => setForm({ ...form, comentarios: e.target.value })} />
            </Field>
          </div>

          <div className="mt-3 flex flex-wrap gap-2">
            <Button variant="success" type="submit" loading={enviando}>{enviando ? 'Enviando…' : 'Enviar evaluación'}</Button>
            <Button to="/plan" variant="subtle">Volver al plan</Button>
          </div>
        </Card>
      )}

      <Card>
        <h2 className="text-base font-semibold">Evaluaciones registradas</h2>
        {evaluaciones.length === 0 ? (
          <p className="text-sm text-gray-500 mt-2">Todavía no has enviado ninguna.</p>
        ) : (
          <DataTable columns={['Iniciativa', '¿Se logró?', 'Dificultad', '¿Repetiría?', 'Comentarios', 'Acciones']}>
            {evaluaciones.map((ev) => (
              <tr key={ev.id}>
                <Td>{tituloDe(ev.iniciativaId)}</Td>
                <Td>{ev.seLogro}</Td>
                <Td>{ev.dificultad}</Td>
                <Td>{ev.repetiria}</Td>
                <Td className="max-w-xs">{ev.comentarios || '—'}</Td>
                <Td>
                  <Button variant="danger" size="xs" onClick={() => eliminar(ev.id)}>Eliminar</Button>
                </Td>
              </tr>
            ))}
          </DataTable>
        )}
      </Card>

      <Card>
        <div className="flex flex-wrap items-center justify-between gap-2">
          <div>
            <h2 className="text-base font-semibold">Análisis de la IA</h2>
            <p className="text-xs text-gray-500">
              La IA lee tus evaluaciones y tus resultados semanales y te dice qué funcionó y qué cambiar.
            </p>
          </div>
          <Button variant="success" loading={analizando} onClick={analizar}>
            {analizando ? 'Analizando…' : analisis ? 'Analizar de nuevo' : 'Analizar con IA'}
          </Button>
        </div>

        {analisis && (
          <div className="mt-3 text-sm space-y-3">
            <p className="text-gray-800">{analisis.resumen}</p>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
              <div className="bg-emerald-50 border border-emerald-200 rounded-xl p-3">
                <p className="text-xs font-semibold text-emerald-800">Lo que funcionó</p>
                <ul className="list-disc pl-5 mt-1 text-gray-700 space-y-1">
                  {analisis.queFunciono.map((t, i) => <li key={i}>{t}</li>)}
                </ul>
              </div>
              <div className="bg-amber-50 border border-amber-200 rounded-xl p-3">
                <p className="text-xs font-semibold text-amber-800">Lo que conviene cambiar</p>
                <ul className="list-disc pl-5 mt-1 text-gray-700 space-y-1">
                  {analisis.queCambiar.map((t, i) => <li key={i}>{t}</li>)}
                </ul>
              </div>
            </div>
            <p className="bg-sky-50 border border-sky-200 rounded-xl p-3 text-gray-800">
              <span className="font-semibold text-sky-800">Siguiente paso: </span>
              {analisis.siguientePaso}
            </p>
          </div>
        )}
      </Card>

      <NoteBox>
        <strong>Nota:</strong> Esta evaluación alimenta futuras sugerencias de la IA. Resume el resultado y los
        aprendizajes; con eso se ajustan las{' '}
        <a className="text-amber-700 underline" href="/iniciativas">iniciativas</a> y el{' '}
        <a className="text-sky-700 underline" href="/plan">plan semanal</a>.
      </NoteBox>
    </>
  )
}
