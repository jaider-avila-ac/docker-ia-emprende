export default function PageHeader({ title, description, children }) {
  return (
    <header className="flex flex-col gap-2 md:flex-row md:items-center md:justify-between">
      <div>
        <h1 className="text-lg font-semibold">{title}</h1>
        {description && <p className="text-xs text-gray-500 mt-0.5">{description}</p>}
      </div>
      {children && <div className="flex flex-wrap gap-2">{children}</div>}
    </header>
  )
}
