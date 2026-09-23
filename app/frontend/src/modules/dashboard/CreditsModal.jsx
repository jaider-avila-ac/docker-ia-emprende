import Modal from '../../components/ui/Modal'

const EQUIPO = [
  'Luis Carlos Restrepo Jimenez',
  'Luis Manuel Zúñiga Pèrez',
  'Helmer Muñoz Hernàndez',
  'Roberto Ferro Escbar',
  'Roberto Carlos Osorio Mass',
  'Mariano Esteban Romero Torres',
  'Kavir Ala Oviedo Priolò',
  'Luz Marlenny Cano Romero',
]

export default function CreditsModal({ open, onClose }) {
  return (
    <Modal open={open} onClose={onClose}>
      <div className="bg-gradient-to-r from-sky-600 to-emerald-500 px-5 py-4 flex items-center gap-3">
        <div className="w-9 h-9 rounded-full bg-white/15 flex items-center justify-center">
          <svg xmlns="http://www.w3.org/2000/svg" className="w-5 h-5 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13 16h-1v-4h-1m1-4h.01M12 3a9 9 0 100 18 9 9 0 000-18z" />
          </svg>
        </div>
        <div className="flex-1">
          <h2 className="text-sm font-semibold text-white tracking-wide">Créditos · IAEmprender</h2>
          <p className="text-[11px] text-white/80">Reconocimiento al esfuerzo colaborativo que hizo posible esta plataforma.</p>
        </div>
        <button type="button" onClick={onClose} className="rounded-full p-1 text-white/80 hover:text-white hover:bg-white/10" aria-label="Cerrar">
          <svg xmlns="http://www.w3.org/2000/svg" className="w-4 h-4" viewBox="0 0 20 20" fill="currentColor">
            <path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd" />
          </svg>
        </button>
      </div>

      <div className="p-5 space-y-4 max-h-[70vh] overflow-y-auto">
        <div className="rounded-xl border border-sky-100 bg-sky-50/60 p-4">
          <h3 className="text-xs font-semibold text-sky-800 uppercase tracking-wide mb-1">Reconocimiento</h3>
          <p className="text-sm text-gray-700 leading-relaxed">
            El diseño de la plataforma IAEmprender es el resultado de un esfuerzo colaborativo y la visión
            compartida de un equipo excepcional de profesionales, investigadores y desarrolladores.
            Extendemos nuestro profundo agradecimiento a las personas y organizaciones cuyo talento,
            dedicación y contribuciones intelectuales han hecho posible esta plataforma.
          </p>
        </div>

        <div className="rounded-xl border border-gray-200 bg-white p-4">
          <div className="flex items-center justify-between gap-2 mb-3">
            <h3 className="text-xs font-semibold text-gray-700 uppercase tracking-wide">Equipo de trabajo</h3>
            <span className="text-[10px] px-2 py-0.5 rounded-full bg-emerald-50 text-emerald-700 border border-emerald-100">
              Colaboración académica
            </span>
          </div>
          <ul className="grid grid-cols-1 sm:grid-cols-2 gap-2 text-sm text-gray-800">
            {EQUIPO.map((nombre) => (
              <li key={nombre} className="flex items-start gap-2">
                <span className="mt-1 w-1.5 h-1.5 rounded-full bg-sky-500" />
                <span>{nombre}</span>
              </li>
            ))}
          </ul>
        </div>

        <div className="flex items-center justify-between gap-3">
          <p className="text-xs text-gray-500">
            Corporación Unificada Nacional de Educación Superior <span className="font-medium">CUN</span>.
          </p>
          <span className="text-[10px] px-2 py-0.5 rounded-full bg-gray-100 text-gray-600 border border-gray-200">
            Proyecto IAEmprender
          </span>
        </div>
      </div>

      <div className="px-5 py-3 flex justify-end bg-gray-50 border-t border-gray-200">
        <button type="button" onClick={onClose} className="text-xs px-3 py-1.5 rounded-lg bg-sky-600 text-white hover:bg-sky-700">
          Cerrar
        </button>
      </div>
    </Modal>
  )
}
