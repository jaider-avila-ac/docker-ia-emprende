from app.modelos.contexto import ContextoNegocio

LIMITE_DESCRIPCION = 500
LIMITE_DIFERENCIADOR = 300
LIMITE_OFERTAS = 8
LIMITE_TONO = 150
LIMITE_PILARES = 5
LIMITE_COMPETIDORES = 5
LIMITE_COMPETIDOR_TEXTO = 200

_CARACTERES_POR_TOKEN = 4

def contar_tokens(texto: str) -> int:
    return max(1, len(texto) // _CARACTERES_POR_TOKEN)

def recortar_contexto(contexto: ContextoNegocio) -> ContextoNegocio:
    recortado = contexto.model_copy()

    if recortado.descripcion and len(recortado.descripcion) > LIMITE_DESCRIPCION:
        recortado.descripcion = recortado.descripcion[:LIMITE_DESCRIPCION] + "…"

    if recortado.diferenciador and len(recortado.diferenciador) > LIMITE_DIFERENCIADOR:
        recortado.diferenciador = recortado.diferenciador[:LIMITE_DIFERENCIADOR] + "…"

    if len(recortado.ofertas) > LIMITE_OFERTAS:
        recortado.ofertas = recortado.ofertas[:LIMITE_OFERTAS]

    if recortado.tono and len(recortado.tono) > LIMITE_TONO:
        recortado.tono = recortado.tono[:LIMITE_TONO] + "…"

    if len(recortado.pilares) > LIMITE_PILARES:
        recortado.pilares = recortado.pilares[:LIMITE_PILARES]

    if len(recortado.competidores) > LIMITE_COMPETIDORES:
        recortado.competidores = recortado.competidores[:LIMITE_COMPETIDORES]
    recortado.competidores = [
        c if len(c) <= LIMITE_COMPETIDOR_TEXTO else c[:LIMITE_COMPETIDOR_TEXTO] + "…" for c in recortado.competidores
    ]

    return recortado
