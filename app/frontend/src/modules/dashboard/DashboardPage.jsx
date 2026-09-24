import { useEffect, useState } from 'react'
import PageHeader from '../../components/ui/PageHeader'
import StatTile from '../../components/ui/StatTile'
import Button from '../../components/ui/Button'
import CreditsModal from './CreditsModal'
import { useBusiness } from '../../context/BusinessContext'
import { ofertasApi } from '../../api/ofertas'
import { apikeysApi } from '../../api/apikeys'
import { inteligenciaApi } from '../../api/inteligencia'
import { planApi } from '../../api/plan'
import { iniciativasApi } from '../../api/iniciativas'

export default function DashboardPage() {
  const { business } = useBusiness()
  const perfilListo = Boolean(business?.descripcion && business?.publicoObjetivo)
  const [creditsOpen, setCreditsOpen] = useState(false)
  const [datos, setDatos] = useState(null)

  useEffect(() => {
    if (!business) {
      setDatos({ ofertas: 0, apiKeyActiva: false, fodaListo: false, smartListo: false, plan: null, iniciativasActivas: 0 })
      return
    }
    Promise.all([
      ofertasApi.listar().catch(() => []),
      apikeysApi.listar().catch(() => []),
      inteligenciaApi.listarFoda().catch(() => []),
      inteligenciaApi.listarSmart().catch(() => []),
      planApi.actual().catch(() => null),
      iniciativasApi.listar().catch(() => []),
    ]).then(([ofertas, apikeys, foda, smart, plan, iniciativas]) => {
      setDatos({
        ofertas: ofertas.length,
        apiKeyActiva: apikeys.some((k) => k.estado === 'activa'),
        fodaListo: foda.length > 0,
        smartListo: smart.length > 0,
        plan,
        iniciativasActivas: iniciativas.filter((i) => ['En prueba', 'Aprobada'].includes(i.estado)).length,
      })
    })
  }, [business])

  const pasos = datos
    ? [perfilListo, datos.ofertas > 0, datos.fodaListo, datos.smartListo, Boolean(datos.plan)]
    : []
  const porcentaje = pasos.length ? Math.round((pasos.filter(Boolean).length / pasos.length) * 100) : 0

  return (
    <>
      <PageHeader title="Dashboard" description="Bienvenido. Completa tu negocio y genera inteligencia.">
        <Button to="/negocio" variant="primary">Configurar datos del negocio</Button>
        <Button to="/inteligencia" variant="subtle">Generar inteligencia (IA)</Button>
      </PageHeader>

      <section className="grid grid-cols-1 md:grid-cols-4 gap-3">
        <StatTile color="emerald" value={perfilListo ? 'Listo' : 'Pendiente'} label="Perfil del negocio" />
        <StatTile color="sky" value={datos ? String(datos.iniciativasActivas) : '—'} label="Iniciativas activas" />
        <StatTile color="amber" value={datos?.plan ? `Sem. ${datos.plan.semanaNumero}` : '—'} label="Plan vigente" />
        <StatTile
          color={datos?.apiKeyActiva ? 'rose' : 'amber'}
          value={datos ? (datos.apiKeyActiva ? 'OK' : 'Falta clave IA') : '—'}
          label="Sin alertas"
        />
      </section>

      <section className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <a href="/negocio" className="group rounded-2xl bg-gray-50 border border-gray-200 p-4 hover:border-emerald-300">
          <div className="flex items-start justify-between">
            <h2 className="text-base font-semibold">Datos del negocio</h2>
            <span className="text-[10px] px-2 py-0.5 rounded bg-sky-500/10 text-sky-700 border border-sky-400/30">Paso 1</span>
          </div>
          <p className="text-sm text-gray-700 mt-2">Completa Perfil, Ofertas, Competidores y Branding.</p>
        </a>

        <a href="/inteligencia" className="group rounded-2xl bg-gray-50 border border-gray-200 p-4 hover:border-sky-300">
          <div className="flex items-start justify-between">
            <h2 className="text-base font-semibold">Generar Inteligencia</h2>
            <span className="text-[10px] px-2 py-0.5 rounded bg-emerald-500/10 text-emerald-700 border border-emerald-400/30">Paso 2</span>
          </div>
          <p className="text-sm text-gray-700 mt-2">FODA · SMART de presencia digital según tus datos.</p>
        </a>
      </section>

      <section className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="rounded-2xl bg-gray-50 border border-gray-200 p-4">
          <h3 className="font-semibold">Acciones rápidas</h3>
          <div className="mt-3 grid grid-cols-2 gap-2">
            <Button to="/negocio/ofertas/nuevo" variant="success" className="text-center">Nueva oferta</Button>
            <Button to="/inteligencia" variant="primary" className="text-center">Generar IA</Button>
            <Button to="/plan" variant="warning" className="text-center">Plan semanal</Button>
            <Button to="/configuracion" variant="dangerSolid" className="text-center">Claves de IA</Button>
          </div>
        </div>

        <div className="rounded-2xl bg-gray-50 border border-gray-200 p-4">
          <h3 className="font-semibold">Progreso de configuración</h3>
          <div className="mt-4">
            <div className="h-2 rounded-full bg-emerald-100">
              <div className="h-2 bg-emerald-500 rounded-full transition-all" style={{ width: `${porcentaje}%` }} />
            </div>
            <p className="text-xs text-gray-500 mt-2">Completado {porcentaje}% (perfil, ofertas, FODA, SMART, plan)</p>
          </div>
        </div>

        <div className="rounded-2xl bg-gray-50 border border-gray-200 p-4">
          <h3 className="font-semibold">Siguiente recomendado</h3>
          <p className="text-sm text-gray-700 mt-2">
            {!perfilListo
              ? 'Completa la descripción y el público de tu negocio.'
              : datos?.ofertas === 0
                ? 'Agrega al menos una oferta.'
                : !datos?.apiKeyActiva
                  ? 'Configura una clave de IA para poder generar FODA/SMART.'
                  : !datos?.fodaListo
                    ? 'Genera tu FODA con IA.'
                    : !datos?.smartListo
                      ? 'Genera tus metas SMART.'
                      : 'Revisa el plan de la semana y qué toca publicar hoy.'}
          </p>
          <Button to={!perfilListo ? '/negocio' : datos?.ofertas === 0 ? '/negocio/ofertas' : !datos?.apiKeyActiva ? '/configuracion' : !datos?.fodaListo ? '/inteligencia/foda' : !datos?.smartListo ? '/inteligencia/smart' : '/plan'} variant="primary" className="inline-flex mt-3">
            Ir ahora
          </Button>
        </div>
      </section>

      <button
        type="button"
        onClick={() => setCreditsOpen(true)}
        className="fixed bottom-4 right-4 z-40 flex items-center gap-2 rounded-full bg-sky-600 text-white text-xs px-4 py-2 shadow-xl hover:bg-sky-700 border border-sky-400"
      >
        <span className="inline-flex items-center justify-center w-5 h-5 rounded-full bg-white/15">
          <svg xmlns="http://www.w3.org/2000/svg" className="w-3.5 h-3.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13 16h-1v-4h-1m1-4h.01M12 3a9 9 0 100 18 9 9 0 000-18z" />
          </svg>
        </span>
        <span>Créditos</span>
      </button>

      <CreditsModal open={creditsOpen} onClose={() => setCreditsOpen(false)} />
    </>
  )
}
