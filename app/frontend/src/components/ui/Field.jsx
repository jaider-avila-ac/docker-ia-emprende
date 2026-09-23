const baseInput =
  'mt-1 w-full bg-gray-50 border border-gray-300 rounded-lg px-3 py-2 focus:outline-none focus:ring-2'

const FOCUS = {
  sky: 'focus:border-sky-400 focus:ring-sky-300',
  emerald: 'focus:border-emerald-400 focus:ring-emerald-300',
  amber: 'focus:border-amber-400 focus:ring-amber-300',
  rose: 'focus:border-rose-400 focus:ring-rose-300',
}

const INVALID = 'border-rose-400 focus:border-rose-400 focus:ring-rose-300'

export function Field({ label, error, className = '', children }) {
  return (
    <label className={`block text-sm ${className}`}>
      {label}
      {children}
      {error && <span className="mt-1 block text-xs text-rose-600">{error}</span>}
    </label>
  )
}

export function Input({ focus = 'emerald', invalid = false, className = '', ...rest }) {
  return <input className={`${baseInput} ${invalid ? INVALID : FOCUS[focus]} ${className}`} {...rest} />
}

export function Textarea({ focus = 'emerald', invalid = false, className = '', rows = 3, ...rest }) {
  return <textarea rows={rows} className={`${baseInput} ${invalid ? INVALID : FOCUS[focus]} ${className}`} {...rest} />
}

export function Select({ focus = 'emerald', invalid = false, className = '', children, ...rest }) {
  return (
    <select className={`${baseInput} ${invalid ? INVALID : FOCUS[focus]} ${className}`} {...rest}>
      {children}
    </select>
  )
}

export function Checkbox({ label, className = '', ...rest }) {
  return (
    <label className={`flex items-center gap-2 text-sm ${className}`}>
      <input type="checkbox" className="accent-sky-600" {...rest} />
      <span>{label}</span>
    </label>
  )
}
