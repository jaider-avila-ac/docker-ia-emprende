import { useEffect, useState } from 'react'
import PageHeader from '../../components/ui/PageHeader'
import Card from '../../components/ui/Card'
import { UserBadge, AiBadge } from '../../components/ui/Badge'
import InteligenciaNavCard from './components/InteligenciaNavCard'
import { inteligenciaApi } from '../../api/inteligencia'

export default function InteligenciaPage() {
  const [fodaListo, setFodaListo] = useState(null)
  const [smartListo, setSmartListo] = useState(null)

  useEffect(() => {
    inteligenciaApi.listarFoda().then((items) => setFodaListo(items.length > 0)).catch(() => setFodaListo(null))
    inteligenciaApi.listarSmart().then((metas) => setSmartListo(metas.length > 0)).catch(() => setSmartListo(null))
  }, [])

  return (
    <>
      <PageHeader
        title="Inteligencia: FODA · SMART"
        description={
          <>
            Primero completa <strong>FODA</strong> y luego define tus <strong>metas SMART</strong> de presencia
            digital.
          </>
        }
      />

      <section className="grid grid-cols-1 lg:grid-cols-2 gap-4">
        <InteligenciaNavCard
          to="/inteligencia/foda"
          color="emerald"
          title="FODA"
          tag={fodaListo ? 'Completado' : 'Empieza aquí'}
          description="Qué tienes a tu favor y qué te frena para tener buena presencia digital."
          hint="Fortalezas, Oportunidades, Debilidades, Amenazas."
        />
        <InteligenciaNavCard
          to="/inteligencia/smart"
          color="amber"
          title="SMART"
          tag={smartListo ? 'Completado' : 'Pendiente'}
          description="Metas claras y con fecha para tu presencia digital."
          hint="Actívalo tras FODA."
        />
      </section>

      <Card className="text-sm">
        <p>
          <UserBadge /> · <AiBadge className="ml-1" />
        </p>
        <p className="text-gray-600 mt-2">
          La IA usa tus <strong>Datos del negocio</strong> para proponer contenido. Puedes pedir nuevas propuestas
          por sección.
        </p>
      </Card>
    </>
  )
}
