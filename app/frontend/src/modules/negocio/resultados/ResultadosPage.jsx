import { useEffect, useState } from 'react'
import PageHeader from '../../../components/ui/PageHeader'
import Card from '../../../components/ui/Card'
import Button from '../../../components/ui/Button'
import Badge from '../../../components/ui/Badge'
import StatTile from '../../../components/ui/StatTile'
import NoteBox from '../../../components/ui/NoteBox'
import DataTable, { Td } from '../../../components/ui/DataTable'
import EmptyState from '../../../components/ui/EmptyState'
import { Field, Input } from '../../../components/ui/Field'
import { useSavedFeedback } from '../../../hooks/useSavedFeedback'
import { validateRequired } from '../../../utils/validation'
import { useBusiness } from '../../../context/BusinessContext'
import { resultadosApi } from '../../../api/resultados'
import { ApiError } from '../../../api/client'

const money = (n) => (n || n === 0 ? `$${Number(n).toLocaleString('es-CO')}` : '—')
const anioActual = new Date().getFullYear()
const emptyForm = { anio: String(anioActual), semanaNumero: '', ingresosAprox: '', clientesAprox: '' }

function Tendencia({ actual, anterior }) {
  if (anterior === undefined) return <span className="text-gray-400">—</span>
  const diferencia = actual - anterior
  if (diferencia > 0) return <span className="text-emerald-600 font-medium">▲ Subió {money(diferencia)}</span>
  if (diferencia < 0) return <span className="text-rose-600 font-medium">▼ Bajó {money(Math.abs(diferencia))}</span>
  return <span className="text-amber-600 font-medium">= Igual que la semana anterior</span>
}

export default function ResultadosPage() {
  const { business, actualizarNegocio } = useBusiness()
  const [resultados, setResultados] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState('')
  const [form, setForm] = useState(emptyForm)
  const [errors, setErrors] = useState({})
  const [guardando, setGuardando] = useState(false)
  const [costosFijos, setCostosFijos] = useState('')
  const costosFeedback = useSavedFeedback()

  const cargar = () => {
    setCargando(true)
    resultadosApi
      .listar()
      .then(setResultados)
      .catch((err) => setError(err instanceof ApiError ? err.message : 'No se pudieron cargar los resultados.'))
      .finally(() => setCargando(false))
  }

  useEffect(cargar, [])
  useEffect(() => setCostosFijos(business?.costosFijosMensuales ?? ''), [business])

  const ultima = resultados[resultados.length - 1]
  const anterior = resultados[resultados.length - 2]

  const agregar = async (e) => {
    e.preventDefault()
    setError('')
    const nextErrors = validateRequired(form, ['anio', 'semanaNumero', 'ingresosAprox'])
    if (!nextErrors.ingresosAprox && Number.isNaN(Number(form.ingresosAprox))) {
      nextErrors.ingresosAprox = 'Debe ser un número.'
    }
    setErrors(nextErrors)
    if (Object.keys(nextErrors).length > 0) return

    setGuardando(true)
    try {
      await resultadosApi.crear(form)
      setForm({ ...emptyForm, anio: form.anio })
      cargar()
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'No se pudo guardar el resultado.')
    } finally {
      setGuardando(false)
    }
  }

  const eliminar = async (id) => {
    setError('')
    try {
      await resultadosApi.eliminar(id)
      setResultados((prev) => prev.filter((r) => r.id !== id))
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'No se pudo eliminar el resultado.')
    }
  }

  const guardarCostos = async (e) => {
    e.preventDefault()
    try {
      await costosFeedback.run(() =>
        actualizarNegocio({ ...business, costosFijosMensuales: costosFijos === '' ? null : Number(costosFijos) }),
      )
    } catch {
    }
  }

  return (
    <>
      <PageHeader
        title="Resultados"
        description="Un pulso simple para saber si lo que hiciste en tus redes esta semana sirvió — no es contabilidad completa."
      />

      {error && <p className="text-xs text-rose-700 bg-rose-50 border border-rose-200 rounded-lg px-3 py-2">{error}</p>}

      <section className="grid grid-cols-1 md:grid-cols-3 gap-3">
        <StatTile color="sky" value={ultima ? money(ultima.ingresosAprox) : '—'} label="Ingresos aprox. última semana" />
        <StatTile color="emerald" value={ultima?.clientesAprox ?? '—'} label="Clientes/pedidos última semana" />
        <StatTile color="amber" value={money(business?.costosFijosMensuales)} label="Costos fijos mensuales (referencia)" />
      </section>

      <Card>
        <div className="flex items-center justify-between">
          <h2 className="text-base font-semibold">Registrar resultado de la semana</h2>
          <span className="text-[10px] px-2 py-0.5 rounded bg-amber-50 text-amber-700 border border-amber-200">
            Aproximado · sin facturas
          </span>
        </div>
        <form onSubmit={agregar} noValidate className="mt-3 grid grid-cols-1 lg:grid-cols-5 gap-3 text-sm">
          <Field label="Año" error={errors.anio}>
            <Input invalid={Boolean(errors.anio)} value={form.anio} onChange={(e) => setForm({ ...form, anio: e.target.value })} />
          </Field>
          <Field label="N.° de semana" error={errors.semanaNumero}>
            <Input
              invalid={Boolean(errors.semanaNumero)}
              placeholder="Ej.: 35"
              value={form.semanaNumero}
              onChange={(e) => setForm({ ...form, semanaNumero: e.target.value })}
            />
          </Field>
          <Field label="Ingresos aproximados (COP)" error={errors.ingresosAprox}>
            <Input
              invalid={Boolean(errors.ingresosAprox)}
              placeholder="0"
              value={form.ingresosAprox}
              onChange={(e) => setForm({ ...form, ingresosAprox: e.target.value })}
            />
          </Field>
          <Field label="Clientes/pedidos aprox. (opcional)">
            <Input
              placeholder="Ej.: 80"
              value={form.clientesAprox}
              onChange={(e) => setForm({ ...form, clientesAprox: e.target.value })}
            />
          </Field>
          <div className="flex items-start pt-5">
            <Button variant="success" type="submit" loading={guardando} className="w-full">
              {guardando ? 'Guardando…' : 'Agregar'}
            </Button>
          </div>
        </form>
      </Card>

      <Card>
        <h2 className="text-base font-semibold">Semana a semana</h2>
        <div className="mt-3">
          {cargando ? (
            <p className="text-sm text-gray-500">Cargando…</p>
          ) : resultados.length === 0 ? (
            <EmptyState title="Aún no has registrado ninguna semana" description="Agrega tu primer resultado para empezar a ver si tus acciones digitales están funcionando." />
          ) : (
            <DataTable columns={['Año', 'Semana', 'Ingresos aprox.', 'Clientes/pedidos', 'Tendencia', 'Acciones']}>
              {resultados.map((r, i) => (
                <tr key={r.id}>
                  <Td>{r.anio}</Td>
                  <Td>{r.semanaNumero}</Td>
                  <Td>{money(r.ingresosAprox)}</Td>
                  <Td>{r.clientesAprox ?? '—'}</Td>
                  <Td><Tendencia actual={r.ingresosAprox} anterior={resultados[i - 1]?.ingresosAprox} /></Td>
                  <Td>
                    <Button variant="danger" size="xs" onClick={() => eliminar(r.id)}>Eliminar</Button>
                  </Td>
                </tr>
              ))}
            </DataTable>
          )}
        </div>
        {anterior && (
          <p className="text-[11px] text-gray-500 mt-2">
            Ejemplo de lectura: si <Badge variant="sky">semana {anterior.semanaNumero}</Badge> e{' '}
            <Badge variant="sky">semana {ultima.semanaNumero}</Badge> quedaron parecidas, lo que hiciste esa semana
            probablemente no movió la aguja — vale la pena revisarlo en{' '}
            <a href="/evaluacion" className="text-sky-700 underline">Evaluación</a>.
          </p>
        )}
      </Card>

      <Card as="form" onSubmit={guardarCostos} className="text-sm">
        <h2 className="text-base font-semibold">Costos fijos mensuales</h2>
        <p className="text-xs text-gray-500 mt-1">
          Solo un número de referencia (arriendo + servicios + lo básico) — no hace falta detallarlo, solo para saber
          cuánto necesitas cubrir al mes.
        </p>
        <div className="flex flex-wrap items-end gap-2 mt-3">
          <Field label="Costos fijos mensuales (COP)" className="w-48">
            <Input value={costosFijos} onChange={(e) => setCostosFijos(e.target.value)} />
          </Field>
          <Button variant="success" type="submit" loading={costosFeedback.saving}>
            {costosFeedback.saving ? 'Guardando…' : 'Guardar'}
          </Button>
          {costosFeedback.saved && <span className="text-xs text-emerald-600">✓ Guardado</span>}
        </div>
      </Card>

      <NoteBox>
        <strong>Nota:</strong> Esta sección <span className="text-emerald-700">NO es generada por IA</span>. Los
        números los escribes tú; la IA los usa para saber si tus iniciativas están funcionando y ajustar lo que te
        sugiere.
      </NoteBox>
    </>
  )
}
