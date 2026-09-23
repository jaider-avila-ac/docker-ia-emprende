import { Link } from 'react-router-dom'

const COLORS = {
  emerald: 'bg-emerald-600',
  sky: 'bg-sky-600',
  amber: 'bg-amber-500',
  rose: 'bg-rose-600',
}

export default function NegocioNavCard({ to, color, title, description }) {
  return (
    <Link to={to} className={`relative rounded-2xl p-4 text-white overflow-hidden block ${COLORS[color]}`}>
      <div className="absolute -bottom-6 -right-6 w-24 h-24 rounded-full bg-black/10" />
      <h2 className="text-base font-semibold relative z-10">{title}</h2>
      <p className="text-white/90 text-xs mt-1 relative z-10">{description}</p>
    </Link>
  )
}
