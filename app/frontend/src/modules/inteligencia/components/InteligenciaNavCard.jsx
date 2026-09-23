import { Link } from 'react-router-dom'

const COLORS = {
  sky: 'bg-sky-600',
  emerald: 'bg-emerald-600',
  amber: 'bg-amber-500',
}

export default function InteligenciaNavCard({ to, color, title, tag, description, hint }) {
  return (
    <Link to={to} className={`relative block rounded-2xl p-4 text-white overflow-hidden hover:opacity-90 ${COLORS[color]}`}>
      <div className="absolute -bottom-6 -right-6 w-24 h-24 rounded-full bg-black/10" />
      <div className="flex items-center justify-between relative z-10">
        <h2 className="text-base font-semibold">{title}</h2>
        <span className="text-[10px] px-2 py-0.5 rounded bg-white/20 text-white border border-white/30">{tag}</span>
      </div>
      <p className="text-sm text-white/90 mt-2 relative z-10">{description}</p>
      <p className="text-[11px] text-white/70 mt-2 relative z-10">{hint}</p>
    </Link>
  )
}
