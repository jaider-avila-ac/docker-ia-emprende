const VARIANTS = {
  sky: 'bg-sky-50 text-sky-700 border-sky-200',
  emerald: 'bg-emerald-50 text-emerald-700 border-emerald-200',
  amber: 'bg-amber-50 text-amber-700 border-amber-200',
  rose: 'bg-rose-50 text-rose-700 border-rose-200',
  gray: 'bg-gray-100 text-gray-600 border-gray-200',
}

export default function Badge({ children, variant = 'gray', className = '' }) {
  return (
    <span
      className={`inline-flex items-center px-2 py-0.5 rounded border text-[10px] ${VARIANTS[variant]} ${className}`}
    >
      {children}
    </span>
  )
}

export function UserBadge(props) {
  return (
    <Badge variant="sky" {...props}>
      Usuario escribe
    </Badge>
  )
}

export function AiBadge(props) {
  return (
    <Badge variant="emerald" {...props}>
      Generado por IA
    </Badge>
  )
}
