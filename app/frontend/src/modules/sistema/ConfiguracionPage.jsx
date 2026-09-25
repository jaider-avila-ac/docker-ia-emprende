import { useEffect, useState } from 'react'
import PageHeader from '../../components/ui/PageHeader'
import Card from '../../components/ui/Card'
import Button from '../../components/ui/Button'
import NoteBox from '../../components/ui/NoteBox'
import { Input } from '../../components/ui/Field'
import { useAuth } from '../../context/AuthContext'
import { apikeysApi } from '../../api/apikeys'
import { ApiError } from '../../api/client'

const PROVEEDORES = [
  { codigo: 'openai', nombre: 'OpenAI', urlClave: 'https://platform.openai.com/api-keys' },
  { codigo: 'gemini', nombre: 'Google Gemini', urlClave: 'https://aistudio.google.com/app/apikey' },
  { codigo: 'deepseek', nombre: 'DeepSeek', urlClave: 'https://platform.deepseek.com/api_keys' },
]

function TarjetaProveedor({ codigo, nombre, urlClave, estado, onGuardado, onEliminado }) {
  const [clave, setClave] = useState('')
  const [guardando, setGuardando] = useState(false)
  const [eliminando, setEliminando] = useState(false)
  const [error, setError] = useState('')

  const guardar = async (e) => {
    e.preventDefault()
    if (!clave.trim()) return
    setError('')
    setGuardando(true)
    try {
      await apikeysApi.guardar(codigo, clave.trim())
      setClave('')
      onGuardado()
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'No se pudo guardar la clave.')
    } finally {
      setGuardando(false)
    }
  }

  const eliminar = async () => {
    setError('')
    setEliminando(true)
    try {
      await apikeysApi.eliminar(codigo)
      onEliminado()
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'No se pudo eliminar la clave.')
    } finally {
      setEliminando(false)
    }
  }

  return (
    <div className="border border-gray-200 rounded-xl p-3">
      <div className="flex items-center justify-between">
        <h3 className="text-sm font-semibold">{nombre}</h3>
        {estado && (
          <span
            className={`text-[10px] px-2 py-0.5 rounded border ${
              estado.estado === 'activa'
                ? 'bg-emerald-50 text-emerald-700 border-emerald-200'
                : 'bg-rose-50 text-rose-700 border-rose-200'
            }`}
          >
            {estado.estado === 'activa' ? 'Activa' : 'Rechazada'}
          </span>
        )}
      </div>

      {estado ? (
        <div className="text-xs text-gray-500 mt-1 space-y-0.5">
          <p>Termina en •••• {estado.claveUltimos4}</p>
          <p>Tokens usados este periodo: {estado.tokensUsadosPeriodo.toLocaleString('es-CO')}</p>
        </div>
      ) : (
        <p className="text-xs text-gray-500 mt-1">Sin configurar todavía.</p>
      )}

      <a
        href={urlClave}
        target="_blank"
        rel="noopener noreferrer"
        className="inline-flex items-center gap-1 text-xs text-sky-700 hover:underline mt-2"
      >
        <i className="bi bi-box-arrow-up-right" aria-hidden="true" />
        Conseguir mi clave de {nombre}
      </a>

      {error && <p className="text-xs text-rose-700 mt-2">{error}</p>}

      <form onSubmit={guardar} className="flex items-center gap-2 mt-3">
        <Input
          type="password"
          placeholder={estado ? 'Reemplazar clave…' : 'Pega tu clave aquí…'}
          value={clave}
          onChange={(e) => setClave(e.target.value)}
          className="flex-1"
        />
        <Button variant="success" type="submit" size="xs" loading={guardando}>
          {estado ? 'Reemplazar' : 'Guardar'}
        </Button>
        {estado && (
          <Button variant="danger" type="button" size="xs" loading={eliminando} onClick={eliminar}>
            Eliminar
          </Button>
        )}
      </form>
    </div>
  )
}

export default function ConfiguracionPage() {
  const { usuario } = useAuth()
  const [estados, setEstados] = useState(null)
  const [error, setError] = useState('')

  const cargar = () => {
    apikeysApi
      .listar()
      .then(setEstados)
      .catch((err) => setError(err instanceof ApiError ? err.message : 'No se pudieron cargar las claves.'))
  }

  useEffect(cargar, [])

  return (
    <>
      <PageHeader title="Preferencias" description="Tu cuenta y las claves de IA que usa este negocio." />

      <section className="grid grid-cols-1 lg:grid-cols-2 gap-4 text-sm">
        <Card className="bg-gray-50">
          <h2 className="text-base font-semibold text-indigo-900">Cuenta</h2>
          {usuario && (
            <dl className="mt-2 space-y-1 text-gray-700">
              <div className="flex justify-between"><dt className="text-gray-500">Nombre</dt><dd>{usuario.nombre} {usuario.apellido}</dd></div>
              <div className="flex justify-between"><dt className="text-gray-500">Correo</dt><dd>{usuario.correo}</dd></div>
            </dl>
          )}
        </Card>

        <Card className="bg-gray-50">
          <h2 className="text-base font-semibold text-indigo-900">Claves de IA (OpenAI / Gemini / DeepSeek)</h2>
          <p className="text-xs text-gray-500 mt-1">
            Necesarias para generar FODA y metas SMART. Se guardan cifradas — nunca se pueden volver a leer, solo
            reemplazar.
          </p>
          {error && <p className="text-xs text-rose-700 mt-2">{error}</p>}
          {estados === null ? (
            <p className="text-xs text-gray-500 mt-2">Cargando…</p>
          ) : (
            <div className="space-y-3 mt-3">
              {PROVEEDORES.map((p) => (
                <TarjetaProveedor
                  key={p.codigo}
                  codigo={p.codigo}
                  nombre={p.nombre}
                  urlClave={p.urlClave}
                  estado={estados.find((e) => e.proveedor === p.codigo)}
                  onGuardado={cargar}
                  onEliminado={cargar}
                />
              ))}
            </div>
          )}
        </Card>
      </section>

      <NoteBox variant="sky">
        <strong>Cómo conseguir una clave:</strong> entra al enlace de tu proveedor preferido, inicia sesión, crea una
        clave nueva, cópiala y pégala en su tarjeta. La clave solo se muestra una vez al crearla, así que cópiala
        antes de cerrar la página.
      </NoteBox>

      <NoteBox>
        Con configurar <strong>una</strong> de las claves alcanza. Si guardas varias, el sistema usa primero
        OpenAI, luego Gemini y luego DeepSeek, y pasa a la siguiente automáticamente si una falla.
      </NoteBox>
    </>
  )
}
