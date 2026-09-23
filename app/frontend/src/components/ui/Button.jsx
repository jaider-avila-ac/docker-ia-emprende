import { Link } from 'react-router-dom'

const VARIANTS = {
  primary: 'bg-sky-600 hover:bg-sky-700 text-white border border-sky-500',
  success: 'bg-emerald-600 hover:bg-emerald-700 text-white border border-emerald-500',
  warning: 'bg-amber-500 hover:bg-amber-600 text-white border border-amber-400/40',
  danger: 'bg-rose-50 hover:bg-rose-100 text-rose-700 border border-rose-200',
  dangerSolid: 'bg-rose-600 hover:bg-rose-700 text-white border border-rose-500',
  neutral: 'bg-gray-200 hover:bg-gray-300 text-gray-800 border border-gray-300',
  subtle: 'bg-gray-100 hover:bg-gray-200 text-gray-700 border border-gray-300',
  ghostSky: 'bg-sky-50 hover:bg-sky-100 text-sky-700 border border-sky-200',
}

const SIZES = {
  xs: 'px-2 py-1 text-xs',
  sm: 'px-3 py-2 text-xs',
  md: 'px-3 py-2 text-sm',
}

function Spinner() {
  return (
    <svg viewBox="0 0 24 24" fill="none" className="w-3.5 h-3.5 shrink-0 animate-spin">
      <circle cx="12" cy="12" r="9" stroke="currentColor" strokeWidth="3" className="opacity-25" />
      <path d="M21 12a9 9 0 0 0-9-9" stroke="currentColor" strokeWidth="3" strokeLinecap="round" />
    </svg>
  )
}

export default function Button({
  variant = 'primary',
  size = 'sm',
  to,
  href,
  loading = false,
  disabled = false,
  className = '',
  children,
  ...rest
}) {
  const isDisabled = disabled || loading
  const classes = `inline-flex items-center justify-center gap-1.5 rounded-lg font-medium transition-colors ${VARIANTS[variant]} ${SIZES[size]} ${
    isDisabled ? 'opacity-60 pointer-events-none' : ''
  } ${className}`

  if (to) {
    return (
      <Link to={to} className={classes} {...rest}>
        {children}
      </Link>
    )
  }
  if (href) {
    return (
      <a href={href} className={classes} {...rest}>
        {children}
      </a>
    )
  }
  return (
    <button type="button" className={classes} disabled={isDisabled} {...rest}>
      {loading && <Spinner />}
      {children}
    </button>
  )
}
