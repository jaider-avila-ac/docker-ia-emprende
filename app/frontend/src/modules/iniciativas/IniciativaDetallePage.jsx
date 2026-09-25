import { useEffect, useState } from 'react'
import { useNavigate, useParams, Link } from 'react-router-dom'
import Card from '../../components/ui/Card'
import Button from '../../components/ui/Button'
import NoteBox from '../../components/ui/NoteBox'
import { Field, Input, Select, Textarea, Checkbox } from '../../components/ui/Field'
import { ESTADO_COLOR } from './data'
import { iniciativasApi } from '../../api/iniciativas'
import { ofertasApi } from '../../api/ofertas'
import { ApiError } from '../../api/client'

export default function IniciativaDetallePage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [iniciativa, setIniciativa] = useState(null)
  const [ofertas, setOfertas] = useState([])
  const [estado, setEstado] = useState('')
  const [titulo, setTitulo] = useState('')
  const [descripcion, setDescripcion] = useState('')
  const [ice, setIce] = useState({ impacto: '3', confianza: '3', esfuerzo: '3' })
  const [notas, setNotas] = useState('')
  const [ofertaIds, setOfertaIds] = useState([])
  const [cargando, setCargando] = useState(true)
  const [guardando, setGuardando] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    setCargando(true)
    Promise.all([iniciativasApi.listar(), ofertasApi.listar()])
      .then(([iniciativas, ofertas]) => {
        const encontrada = iniciativas.find((i) => String(i.id) === id)
        if (!encontrada) {
          setError('Esta iniciativa ya no existe.')
          return
        }
        setIniciativa(encontrada)
        setEstado(encontrada.estado)
        setTitulo(encontrada.titulo)
        setDescripcion(encontrada.descripcion || '')
        setIce({
          impacto: String(encontrada.impacto),
          confianza: String(encontrada.confianza),
          esfuerzo: String(encontrada.esfuerzo),
        })
        setNotas(encontrada.notas || '')
        setOfertaIds(encontrada.ofertaIds || [])
        setOfertas(ofertas)
      })
      .catch(() => setError('No se pudo cargar la iniciativa.'))
      .finally(() => setCargando(false))
  }, [id])

  const toggleOferta = (ofertaId) =>
    setOfertaIds((prev) => (prev.includes(ofertaId) ? prev.filter((o) => o !== ofertaId) : [...prev, ofertaId]))

  const guardar = async () => {
    setError('')
    if (!titulo.trim()) {
      setError('El título no puede quedar vacío.')
      return
    }
    for (const valor of Object.values(ice)) {
      const n = Number(valor)
      if (!Number.isInteger(n) || n < 1 || n > 5) {
        setError('Impacto, confianza y esfuerzo deben ser números de 1 a 5.')
        return
      }
    }
    setGuardando(true)
    try {
      const actualizada = await iniciativasApi.actualizar(id, {
        ...iniciativa,
        ...ice,
        titulo: titulo.trim(),
        descripcion,
        estado,
        notas,
        ofertaIds,
      })
      setIniciativa(actualizada)
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'No se pudo guardar.')
    } finally {
      setGuardando(false)
    }
  }

  const eliminar = async () => {
    try {
      await iniciativasApi.eliminar(id)
      navigate('/iniciativas')
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'No se pudo eliminar.')
    }
  }

  if (cargando) return <p className="text-sm text-gray-500">Cargando…</p>
  if (!iniciativa) {
    return (
      <>
        <p className="text-sm text-rose-700">{error || 'Iniciativa no encontrada.'}</p>
        <Link to="/iniciativas" className="text-xs text-amber-600 hover:underline">← Volver a Iniciativas</Link>
      </>
    )
  }

  const ofertasVinculadas = ofertas.filter((o) => ofertaIds.includes(o.id))

  return (
    <>
      <header className="flex items-center justify-between">
        <div>
          <Link to="/iniciativas" className="text-xs text-amber-600 hover:underline">← Volver a Iniciativas</Link>
          <h1 className="text-lg font-semibold mt-1">{iniciativa.titulo}</h1>
          <p className="text-xs text-gray-500">
            Meta: {iniciativa.metaTipo || '—'} · Estado:{' '}
            <span className={`font-medium ${ESTADO_COLOR[estado]}`}>{estado}</span> · ICE: {iniciativa.ice.toFixed(1)}
          </p>
        </div>
        <div className="flex gap-2">
          <Button to="/plan" variant="ghostSky">Ir al plan semanal</Button>
          <Button variant="danger" onClick={eliminar}>Eliminar</Button>
        </div>
      </header>

      {error && <p className="text-xs text-rose-700 bg-rose-50 border border-rose-200 rounded-lg px-3 py-2">{error}</p>}

      <section className="grid grid-cols-1 lg:grid-cols-[2fr_1fr] gap-4">
        <Card as="article" className="text-sm">
          <Field label="Título">
            <Input focus="amber" value={titulo} onChange={(e) => setTitulo(e.target.value)} />
          </Field>
          <Field label="Descripción" className="block mt-3">
            <Textarea focus="amber" rows={3} value={descripcion} onChange={(e) => setDescripcion(e.target.value)} />
          </Field>
          <div className="grid grid-cols-3 gap-2 mt-3">
            <Field label="Impacto (1-5)">
              <Input focus="amber" value={ice.impacto} onChange={(e) => setIce({ ...ice, impacto: e.target.value })} />
            </Field>
            <Field label="Confianza (1-5)">
              <Input focus="amber" value={ice.confianza} onChange={(e) => setIce({ ...ice, confianza: e.target.value })} />
            </Field>
            <Field label="Esfuerzo (1-5)">
              <Input focus="amber" value={ice.esfuerzo} onChange={(e) => setIce({ ...ice, esfuerzo: e.target.value })} />
            </Field>
          </div>

          <div className="mt-3">
            <h3 className="font-semibold text-sm">Ofertas vinculadas</h3>
            {ofertasVinculadas.length === 0 ? (
              <p className="text-gray-500 text-xs mt-2">Ninguna todavía.</p>
            ) : (
              <ul className="list-disc pl-5 text-gray-700 mt-2">
                {ofertasVinculadas.map((o) => (
                  <li key={o.id}>{o.nombre}</li>
                ))}
              </ul>
            )}
            {ofertas.length > 0 && (
              <div className="grid grid-cols-2 gap-1 mt-2">
                {ofertas.map((o) => (
                  <Checkbox key={o.id} label={o.nombre} checked={ofertaIds.includes(o.id)} onChange={() => toggleOferta(o.id)} />
                ))}
              </div>
            )}
          </div>

          <div className="mt-4">
            <h3 className="font-semibold text-sm">Notas</h3>
            <Textarea focus="amber" rows={3} className="mt-2" value={notas} onChange={(e) => setNotas(e.target.value)} />
          </div>
        </Card>

        <Card as="aside" className="text-sm">
          <h3 className="font-semibold text-sm">Acciones</h3>
          <div className="mt-2 grid grid-cols-1 gap-2">
            <Field label="Estado">
              <Select focus="amber" value={estado} onChange={(e) => setEstado(e.target.value)}>
                <option>Propuesta</option>
                <option>En prueba</option>
                <option>Aprobada</option>
                <option>Descartada</option>
              </Select>
            </Field>
            <Button variant="success" loading={guardando} onClick={guardar}>
              {guardando ? 'Guardando…' : 'Guardar cambios'}
            </Button>
            <Button to="/plan" variant="ghostSky" className="text-center">Ir al plan semanal</Button>
          </div>

          {iniciativa.iaTip && (
            <div className="mt-4 bg-gray-50 border border-gray-200 rounded-xl p-3">
              <p className="text-[11px] text-gray-500">Sugerencia de la IA</p>
              <p className="text-sm text-gray-700">{iniciativa.iaTip}</p>
            </div>
          )}
        </Card>
      </section>

      <NoteBox>
        Esta iniciativa la propuso la <span className="text-emerald-700">IA</span>. Edita lo que quieras, vincula
        ofertas y cambia su estado; déjala como está si te sirve así.
      </NoteBox>
    </>
  )
}
