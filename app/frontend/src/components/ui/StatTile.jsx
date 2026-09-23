const COLORS = {
  emerald: 'bg-emerald-600',
  sky: 'bg-sky-600',
  amber: 'bg-amber-500',
  rose: 'bg-rose-600',
  indigo: 'bg-indigo-600',
}

export default function StatTile({ value, label, color = 'sky' }) {
  return (
    <div className={`relative rounded-2xl p-4 text-white overflow-hidden ${COLORS[color]}`}>
      <div className="absolute -bottom-6 -right-6 w-24 h-24 rounded-full bg-black/10" />
      <p className="text-xl font-semibold relative z-10">{value}</p>
      <p className="text-white/90 text-xs mt-0.5 relative z-10">{label}</p>
    </div>
  )
}
