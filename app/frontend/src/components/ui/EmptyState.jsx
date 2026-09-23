export default function EmptyState({ title = 'Nada por aquí todavía', description, action }) {
  return (
    <div className="flex flex-col items-center text-center gap-2 py-10 px-4">
      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" className="w-9 h-9 text-gray-300">
        <path strokeLinecap="round" strokeLinejoin="round" d="M3 7h5l2 2h11v10a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V7Z" />
      </svg>
      <p className="text-sm font-medium text-gray-600">{title}</p>
      {description && <p className="text-xs text-gray-500 max-w-xs">{description}</p>}
      {action && <div className="mt-1">{action}</div>}
    </div>
  )
}
