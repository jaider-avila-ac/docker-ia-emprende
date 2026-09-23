import { useEffect, useState } from 'react'
import PageHeader from '../../../components/ui/PageHeader'
import Card from '../../../components/ui/Card'
import Button from '../../../components/ui/Button'
import { UserBadge } from '../../../components/ui/Badge'
import NoteBox from '../../../components/ui/NoteBox'
import DataTable, { Td } from '../../../components/ui/DataTable'
import EmptyState from '../../../components/ui/EmptyState'
import { money } from './data'
import { ofertasApi } from '../../../api/ofertas'
import { ApiError } from '../../../api/client'

export default function OfertasListPage() {
  const [ofertas, setOfertas] = useState([])
  const [cargando, setCargando] = useState(true)
  const [error, setError] = useState('')

  const cargar = () => {
    setCargando(true)
    ofertasApi
      .listar()
      .then(setOfertas)
      .catch((err) => setError(err instanceof ApiError ? err.message : 'No se pudieron cargar las ofertas.'))
      .finally(() => setCargando(false))
  }

  useEffect(cargar, [])

  const duplicar = async (oferta) => {
    setError('')
    try {
      await ofertasApi.crear({ ...oferta, nombre: `${oferta.nombre} (copia)` })
      cargar()
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'No se pudo duplicar la oferta.')
    }
  }

  const eliminar = async (id) => {
    setError('')
    try {
      await ofertasApi.eliminar(id)
      setOfertas((prev) => prev.filter((o) => o.id !== id))
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'No se pudo eliminar la oferta.')
    }
  }

  return (
    <>
      <PageHeader title="Ofertas (productos/servicios)" description={<UserBadge />}>
        <Button to="/negocio/ofertas/nuevo" variant="primary">Nueva oferta</Button>
        <Button to="/negocio" variant="neutral">← Volver a Datos del negocio</Button>
      </PageHeader>

      {error && <p className="text-xs text-rose-700 bg-rose-50 border border-rose-200 rounded-lg px-3 py-2">{error}</p>}

      <Card>
        {cargando ? (
          <p className="text-sm text-gray-500">Cargando…</p>
        ) : ofertas.length === 0 ? (
          <EmptyState
            title="Aún no tienes ofertas"
            description="Agrega tus productos o servicios principales para tener de dónde sacar ideas de contenido."
            action={<Button to="/negocio/ofertas/nuevo" variant="primary">Nueva oferta</Button>}
          />
        ) : (
          <DataTable columns={['Tipo', 'Nombre', 'Categoría', 'Precio', 'Qué lo hace especial', 'Acciones']}>
            {ofertas.map((o) => (
              <tr key={o.id} className="hover:bg-gray-50">
                <Td>{o.tipo}</Td>
                <Td>{o.nombre}</Td>
                <Td>{o.categoria}</Td>
                <Td>{money(o.precio)}</Td>
                <Td className="max-w-xs">{o.destacar || '—'}</Td>
                <Td className="flex gap-1">
                  <Button to={`/negocio/ofertas/${o.id}/editar`} variant="primary" size="xs">Editar</Button>
                  <Button variant="subtle" size="xs" onClick={() => duplicar(o)}>Duplicar</Button>
                  <Button variant="danger" size="xs" onClick={() => eliminar(o.id)}>Eliminar</Button>
                </Td>
              </tr>
            ))}
          </DataTable>
        )}

        <NoteBox className="mt-3">
          <strong>Nota:</strong> Esta sección la completa el <span className="text-sky-700 font-medium">usuario</span>.
          La IA la usa para saber qué mencionar y qué mostrar en tus publicaciones.
        </NoteBox>
      </Card>
    </>
  )
}
