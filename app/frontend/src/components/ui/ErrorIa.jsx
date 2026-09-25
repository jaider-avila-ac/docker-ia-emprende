export default function ErrorIa({ mensaje }) {
  if (!mensaje) return null
  return (
    <p className="text-xs text-rose-700 bg-rose-50 border border-rose-200 rounded-lg px-3 py-2">
      {mensaje}
      {/clave/i.test(mensaje) && (
        <>
          {' '}
          <a href="/configuracion" className="underline font-medium">Ir a Configuración →</a>
        </>
      )}
    </p>
  )
}
