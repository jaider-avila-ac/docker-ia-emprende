import { useEffect, useState } from 'react'
import PageHeader from '../../components/ui/PageHeader'
import Card from '../../components/ui/Card'
import Button from '../../components/ui/Button'
import { AiBadge } from '../../components/ui/Badge'
import NoteBox from '../../components/ui/NoteBox'
import DataTable, { Td } from '../../components/ui/DataTable'
import EmptyState from '../../components/ui/EmptyState'
import ErrorIa from '../../components/ui/ErrorIa'
import { ESTADO_COLOR } from './data'
import { iniciativasApi } from '../../api/iniciativas'
import { inteligenciaApi } from '../../api/inteligencia'
import { ApiError } from '../../api/client'

export default function IniciativasPage() {
  const [iniciativas, setIniciativas] = useState([])
  const [cargando, setCargando] = useState(true)
  const [generando, setGenerando] = useState(false)
  const [error, setError] = useState('')

  const mensajeDe = (err, porDefecto) => (err instanceof ApiError ? err.message : porDefecto)

  useEffect(() => {
    iniciativasApi
      .listar()
      .then(setIniciativas)
      .catch((err) => setError(mensajeDe(err, 'No se pudieron cargar las iniciativas.')))
      .finally(() => setCargando(false))
  }, [])

  const generar = async () => {
    setError('')
    setGenerando(true)
    try {
      await inteligenciaApi.generarIniciativas()
      setIniciativas(await iniciativasApi.listar())
    } catch (err) {
      setError(mensajeDe(err, 'No se pudieron generar las iniciativas.'))
    } finally {
      setGenerando(false)
    }
  }

  const eliminar = async (id) => {
    setError('')
    try {
      await iniciativasApi.eliminar(id)
      setIniciativas((prev) => prev.filter((i) => i.id !== id))
    } catch (err) {
      setError(mensajeDe(err, 'No se pudo eliminar la iniciativa.'))
    }
  }

  return (
    <>
      <PageHeader title="Iniciativas · lista priorizada (ICE)" description={<AiBadge />}>
        <Button variant="success" loading={generando} onClick={generar}>
          {generando ? 'Generando… (puede tardar hasta 1 min)' : 'Generar iniciativas con IA'}
        </Button>
        <Button to="/plan" variant="ghostSky">Ir al plan semanal</Button>
      </PageHeader>

      <ErrorIa mensaje={error} />

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
              description="La IA las propone a partir de tu FODA, tus metas SMART y lo que aprendas en las evaluaciones. Primero genera el FODA o las metas SMART."
              action={
                <Button variant="success" loading={generando} onClick={generar}>
                  {generando ? 'Generando…' : 'Generar iniciativas con IA'}
                </Button>
              }
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
                    <Button to={`/iniciativas/${i.id}`} variant="primary" size="xs">Ver y editar</Button>
                    <Button variant="danger" size="xs" onClick={() => eliminar(i.id)}>Quitar</Button>
                  </Td>
                </tr>
              ))}
            </DataTable>
          )}
        </div>

        <p className="text-[11px] text-gray-500 mt-2">
          Revisa las propuestas: entra a cada una para editarla, cámbiala a "Aprobada" o "En prueba" las que quieras
          trabajar, y quita las que no te sirvan. Luego genera el plan semanal.
        </p>
      </Card>

      {iniciativas.length > 0 && (
        <NoteBox>
          Al pulsar "Generar iniciativas con IA" otra vez, la IA agrega propuestas nuevas sin repetir las que ya
          tienes, y tiene en cuenta tus evaluaciones.
        </NoteBox>
      )}
    </>
  )
}
