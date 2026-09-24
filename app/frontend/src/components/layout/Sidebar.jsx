import { useEffect, useMemo, useState } from 'react'
import { NavLink, useLocation, useNavigate } from 'react-router-dom'
import { useBusiness } from '../../context/BusinessContext'
import { useAuth } from '../../context/AuthContext'

const TREE = [
  { type: 'file', to: '/', label: 'Dashboard', end: true, icon: 'bi-speedometer2' },
  {
    type: 'folder',
    label: 'Datos del negocio',
    icon: 'bi-shop',
    children: [
      { type: 'file', to: '/negocio', label: 'Perfil', end: true },
      { type: 'file', to: '/negocio/ofertas', label: 'Ofertas' },
      { type: 'file', to: '/negocio/competidores', label: 'Competidores' },
      { type: 'file', to: '/negocio/branding', label: 'Branding' },
      { type: 'file', to: '/negocio/resultados', label: 'Resultados' },
    ],
  },
  {
    type: 'folder',
    label: 'Inteligencia',
    icon: 'bi-stars',
    children: [
      { type: 'file', to: '/inteligencia', label: 'Resumen', end: true },
      { type: 'file', to: '/inteligencia/foda', label: 'FODA' },
      { type: 'file', to: '/inteligencia/smart', label: 'SMART' },
    ],
  },
  {
    type: 'folder',
    label: 'Ejecución',
    icon: 'bi-rocket-takeoff',
    children: [
      { type: 'file', to: '/iniciativas', label: 'Iniciativas' },
      {
        type: 'folder',
        label: 'Plan semanal',
        icon: 'bi-calendar-week',
        children: [
          { type: 'file', to: '/plan', label: 'Resumen', end: true },
          { type: 'file', to: '/plan/ajustes', label: 'Ajustes' },
          { type: 'file', to: '/plan/semana/34', label: 'Semana 34' },
        ],
      },
      { type: 'file', to: '/evaluacion', label: 'Evaluación' },
    ],
  },
  {
    type: 'folder',
    label: 'Sistema',
    icon: 'bi-gear',
    children: [
      { type: 'file', to: '/configuracion', label: 'Configuración' },
    ],
  },
]

const isFileActive = (node, pathname) =>
  node.end ? pathname === node.to : pathname === node.to || pathname.startsWith(`${node.to}/`)

function collectFolderLabels(nodes, pathname = null) {
  const labels = []
  const walk = (list) => {
    let matched = false
    for (const node of list) {
      if (node.type === 'file') {
        if (pathname !== null && isFileActive(node, pathname)) matched = true
      } else {
        const childMatched = walk(node.children)
        if (pathname === null || childMatched) {
          labels.push(node.label)
          matched = matched || childMatched || pathname === null
        }
      }
    }
    return matched
  }
  walk(nodes)
  return labels
}

const ALL_FOLDER_LABELS = collectFolderLabels(TREE)

function ChevronIcon({ open }) {
  return (
    <svg
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
      className={`w-3 h-3 shrink-0 text-indigo-400 transition-transform duration-150 ${open ? 'rotate-90' : ''}`}
    >
      <path strokeLinecap="round" strokeLinejoin="round" d="M9 6l6 6-6 6" />
    </svg>
  )
}

function NodeIcon({ icon, className }) {
  return <i className={`bi ${icon} ${className}`} aria-hidden="true" />
}

function TreeFile({ node, onNavigate }) {
  return (
    <NavLink
      to={node.to}
      end={node.end}
      onClick={onNavigate}
      className={({ isActive }) =>
        `flex items-center gap-2 pl-2 pr-2 py-1.5 rounded-md mt-0.5 text-sm truncate ${
          isActive ? 'bg-sky-600 text-white' : 'text-indigo-100 hover:bg-indigo-800/50'
        }`
      }
    >
      <NodeIcon icon={node.icon || 'bi-dot'} className="shrink-0 text-indigo-300" />
      <span className="truncate">{node.label}</span>
    </NavLink>
  )
}

function TreeFolder({ node, openFolders, onToggle, activeFolders, onNavigate }) {
  const isOpen = openFolders.has(node.label)
  const isAncestorOfActive = activeFolders.has(node.label)

  return (
    <div>
      <button
        type="button"
        onClick={() => onToggle(node.label)}
        className={`w-full flex items-center gap-1.5 pl-1 pr-2 py-1.5 rounded-md mt-0.5 text-sm text-left select-none ${
          isAncestorOfActive ? 'text-white font-medium' : 'text-indigo-200 hover:bg-indigo-800/50'
        }`}
      >
        <ChevronIcon open={isOpen} />
        <NodeIcon icon={node.icon} className="shrink-0 text-sky-300" />
        <span className="truncate">{node.label}</span>
      </button>

      {isOpen && (
        <div className="ml-3 pl-2 border-l border-indigo-800/60 space-y-0">
          {node.children.map((child) => (
            <TreeNode
              key={child.to ?? child.label}
              node={child}
              openFolders={openFolders}
              onToggle={onToggle}
              activeFolders={activeFolders}
              onNavigate={onNavigate}
            />
          ))}
        </div>
      )}
    </div>
  )
}

function TreeNode(props) {
  return props.node.type === 'file' ? (
    <TreeFile node={props.node} onNavigate={props.onNavigate} />
  ) : (
    <TreeFolder {...props} />
  )
}

export default function Sidebar({ onNavigate }) {
  const { business, salir } = useBusiness()
  const { usuario, logout } = useAuth()
  const location = useLocation()
  const navigate = useNavigate()

  const cerrarSesion = () => {
    logout()
    navigate('/login', { replace: true })
  }

  const cambiarNegocio = () => {
    salir()
    navigate('/negocios')
  }

  const [openFolders, setOpenFolders] = useState(() => new Set(ALL_FOLDER_LABELS))

  const activeFolders = useMemo(
    () => new Set(collectFolderLabels(TREE, location.pathname)),
    [location.pathname],
  )

  useEffect(() => {
    if (activeFolders.size === 0) return
    setOpenFolders((prev) => {
      const next = new Set(prev)
      let changed = false
      activeFolders.forEach((label) => {
        if (!next.has(label)) {
          next.add(label)
          changed = true
        }
      })
      return changed ? next : prev
    })
  }, [activeFolders])

  const toggleFolder = (label) => {
    setOpenFolders((prev) => {
      const next = new Set(prev)
      if (next.has(label)) next.delete(label)
      else next.add(label)
      return next
    })
  }

  return (
    <aside
      className="text-indigo-50 bg-gradient-to-b from-indigo-900 via-indigo-950 to-indigo-900
                 border-r border-indigo-800 h-full md:sticky md:top-0 md:h-screen
                 overflow-y-auto flex flex-col"
    >
      <div className="p-4 flex items-center gap-3">
        <div className="h-9 w-9 rounded-xl grid place-content-center font-semibold text-white bg-sky-600">
          IA
        </div>
        <div>
          <p className="text-sm font-semibold text-white">IAEmprender</p>
          <p className="text-xs text-indigo-300">Panel</p>
        </div>
      </div>

      <div className="px-4">
        <div className="text-[11px] text-indigo-300">Negocio activo</div>
        <div className="mt-1 px-3 py-2 rounded-lg bg-indigo-900/40 border border-indigo-800 text-xs text-indigo-100">
          <span className="truncate block">{business?.nombre}</span>
          {business?.rubro && <p className="text-indigo-300 mt-0.5">{business.rubro}</p>}
          <button
            type="button"
            onClick={cambiarNegocio}
            className="mt-2 text-indigo-300 hover:text-white hover:underline"
          >
            <i className="bi bi-arrow-left-right mr-1" aria-hidden="true" />
            Cambiar de negocio
          </button>
        </div>
      </div>

      <nav className="px-2 pb-4 mt-3 flex-1">
        {TREE.map((node) => (
          <TreeNode
            key={node.to ?? node.label}
            node={node}
            openFolders={openFolders}
            onToggle={toggleFolder}
            activeFolders={activeFolders}
            onNavigate={onNavigate}
          />
        ))}
      </nav>

      <div className="px-4 py-3 border-t border-indigo-800/60">
        {usuario && <p className="text-xs text-indigo-200 truncate">{usuario.nombre} {usuario.apellido}</p>}
        <button
          type="button"
          onClick={cerrarSesion}
          className="text-xs text-indigo-300 hover:text-white hover:underline mt-0.5"
        >
          Cerrar sesión
        </button>
      </div>
    </aside>
  )
}
