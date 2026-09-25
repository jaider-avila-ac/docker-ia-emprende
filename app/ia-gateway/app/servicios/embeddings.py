from functools import lru_cache

import numpy as np
from sentence_transformers import SentenceTransformer

from app.modelos.contexto import ContextoNegocio

_NOMBRE_MODELO = "sentence-transformers/all-MiniLM-L6-v2"

UMBRAL_SIMILITUD = 0.95

@lru_cache(maxsize=1)
def _modelo() -> SentenceTransformer:
    return SentenceTransformer(_NOMBRE_MODELO)

def texto_de_contexto(contexto: ContextoNegocio) -> str:
    partes = [
        contexto.nombre,
        contexto.rubro,
        contexto.descripcion or "",
        contexto.publico_objetivo or "",
        contexto.diferenciador or "",
        ", ".join(contexto.ofertas),
        contexto.tono or "",
        ", ".join(contexto.pilares),
    ]
    return " | ".join(partes)

def similitud(texto_a: str, texto_b: str) -> float:
    vectores = _modelo().encode([texto_a, texto_b], normalize_embeddings=True)
    return float(np.dot(vectores[0], vectores[1]))
