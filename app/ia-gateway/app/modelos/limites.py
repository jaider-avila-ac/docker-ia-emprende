import unicodedata

FODA_ITEM = 300

SMART_TITULO = 150
SMART_NUMERO_META = 150
SMART_ESPECIFICO = 400
SMART_MEDICION = 300
SMART_PASOS = 500

INICIATIVA_TITULO = 120
INICIATIVA_DESCRIPCION = 400
INICIATIVA_TIP = 300

ACCION_PLAN = 200

ANALISIS_RESUMEN = 600
ANALISIS_ITEM = 250
ANALISIS_PASO = 300

META_TIPOS = ("Constancia", "Presentación", "Interacción", "Alcance")
PILARES = ("Educativo", "Oferta", "Prueba social", "Interacción", "Servicio")
DIAS = ("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")


def _normal(texto: str) -> str:
    sin_tildes = "".join(c for c in unicodedata.normalize("NFD", texto) if unicodedata.category(c) != "Mn")
    return sin_tildes.strip().casefold()


def canonico(valor, opciones: tuple[str, ...]) -> str | None:
    buscado = _normal(valor) if isinstance(valor, str) else None
    return next((opcion for opcion in opciones if _normal(opcion) == buscado), None)
