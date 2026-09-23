const VARIANTS = {
  gray: 'bg-gray-50 border-gray-200 text-gray-600',
  emerald: 'bg-emerald-50 border-emerald-200 text-gray-700',
  sky: 'bg-sky-50 border-sky-200 text-gray-700',
  amber: 'bg-amber-50 border-amber-200 text-gray-700',
  rose: 'bg-rose-50 border-rose-200 text-gray-700',
}

export default function NoteBox({ variant = 'gray', title, children, className = '' }) {
  return (
    <div className={`rounded-2xl border p-3 text-[11px] ${VARIANTS[variant]} ${className}`}>
      {title && <p className="font-semibold mb-1">{title}</p>}
      <p>{children}</p>
    </div>
  )
}
