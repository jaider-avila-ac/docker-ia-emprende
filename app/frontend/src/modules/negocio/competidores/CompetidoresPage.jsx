import { useEffect, useState } from 'react'
import PageHeader from '../../../components/ui/PageHeader'
import Card from '../../../components/ui/Card'
import Button from '../../../components/ui/Button'
import { UserBadge } from '../../../components/ui/Badge'
import DataTable, { Td } from '../../../components/ui/DataTable'
import EmptyState from '../../../components/ui/EmptyState'
import { Field, Input } from '../../../components/ui/Field'
import { validateRequired } from '../../../utils/validation'
import { competidoresApi } from '../../../api/competidores'
import { ApiError } from '../../../api/client'

const emptyForm = { nombre: '', canal: '', notas: '' }

export default function CompetidoresPage() {
  const [competidores, setCompetidores] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState('')
  const [form, setForm] = useState(emptyForm)
  const [errors, setErrors] = useState({})
  const [editandoId, setEditandoId] = useState(null)
  const [guardando, setGuardando] = useState(false)

  const cargar = () => {
    setCargando(true)
    competidoresApi
      .listar()
      .then(setCompetidores)
      .catch((err) => setError(err instanceof ApiError ? err.message : 'No se pudieron cargar los competidores.'))
      .finally(() => setCargando(false))
  }

  useEffect(cargar, [])

  const editar = (competidor) => {
    setEditandoId(competidor.id)
    setForm({ nombre: competidor.nombre, canal: competidor.canal || '', notas: competidor.notas || '' })
    setErrors({})
  }

  const cancelarEdicion = () => {
    setEditandoId(null)
    setForm(emptyForm)
    setErrors({})
  }

  const guardar = async (e) => {
    e.preventDefault()
    setError('')
    const nextErrors = validateRequired(form, ['nombre'])
    setErrors(nextErrors)
    if (Object.keys(nextErrors).length > 0) return

    setGuardando(true)
    try {
      if (editandoId) {
        await competidoresApi.actualizar(editandoId, form)
      } else {
        await competidoresApi.crear(form)
      }
      setForm(emptyForm)
      setEditandoId(null)
      cargar()
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'No se pudo guardar el competidor.')
    } finally {
      setGuardando(false)
    }
  }

  const eliminar = async (id) => {
    setError('')
    try {
      await competidoresApi.eliminar(id)
      setCompetidores((prev) => prev.filter((c) => c.id !== id))
      if (editandoId === id) cancelarEdicion()
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'No se pudo eliminar el competidor.')
    }
  }

  return (
    <>
      <PageHeader
        title="Competidores / Referencias"
        description={
          <>
            Qué hacen otros negocios parecidos en redes. <UserBadge className="ml-1" />
          </>
        }
      />

      {error && <p className="text-xs text-rose-700 bg-rose-50 border border-rose-200 rounded-lg px-3 py-2">{error}</p>}

      <Card as="form" onSubmit={guardar} noValidate className="grid grid-cols-1 lg:grid-cols-4 gap-3 text-sm">
        <Field label="Nombre" error={errors.nombre}>
          <Input
            invalid={Boolean(errors.nombre)}
            placeholder="Ej.: Restaurante El Sazón"
            value={form.nombre}
            onChange={(e) => setForm({ ...form, nombre: e.target.value })}
          />
        </Field>
        <Field label="Dónde tiene presencia">
          <Input
            placeholder="Ej.: Instagram, ~1.200 seguidores"
            value={form.canal}
            onChange={(e) => setForm({ ...form, canal: e.target.value })}
          />
        </Field>
        <Field label="¿Qué le funciona (o no)?">
          <Input
            placeholder="Ej.: Publica seguido, buena respuesta"
            value={form.notas}
            onChange={(e) => setForm({ ...form, notas: e.target.value })}
          />
        </Field>
        <div className="flex items-start gap-2 pt-5">
          <Button variant="success" type="submit" loading={guardando} className="w-full">
            {guardando ? 'Guardando…' : editandoId ? 'Actualizar' : 'Agregar'}
          </Button>
          {editandoId && (
            <Button variant="subtle" type="button" onClick={cancelarEdicion}>
              Cancelar
            </Button>
          )}
        </div>
      </Card>

      <Card>
        {cargando ? (
          <p className="text-sm text-gray-500">Cargando…</p>
        ) : competidores.length === 0 ? (
          <EmptyState
            title="Aún no registraste referencias"
            description="Agrega 2 o 3 negocios parecidos al tuyo para ver qué les funciona en redes y qué puedes aprovechar tú."
          />
        ) : (
          <>
            <DataTable columns={['Nombre', 'Dónde tiene presencia', 'Qué le funciona', 'Acciones']}>
              {competidores.map((c) => (
                <tr key={c.id} className={`hover:bg-amber-50 ${editandoId === c.id ? 'bg-amber-50' : ''}`}>
                  <Td>{c.nombre}</Td>
                  <Td>{c.canal}</Td>
                  <Td>{c.notas}</Td>
                  <Td className="flex gap-1">
                    <Button variant="ghostSky" size="xs" onClick={() => editar(c)}>Editar</Button>
                    <Button variant="danger" size="xs" onClick={() => eliminar(c.id)}>Eliminar</Button>
                  </Td>
                </tr>
              ))}
            </DataTable>
            <p className="text-[11px] text-gray-500 mt-2">
              Esto ayuda a la IA a proponer ideas realistas de presencia digital, no copiar sino aprender qué funciona en tu zona.
            </p>
          </>
        )}
      </Card>
    </>
  )
}
