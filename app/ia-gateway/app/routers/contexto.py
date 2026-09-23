from fastapi import APIRouter
from pydantic import BaseModel

from app.modelos.contexto import ContextoNegocio
from app.servicios.embeddings import UMBRAL_SIMILITUD, similitud, texto_de_contexto

router = APIRouter(prefix="/contexto", tags=["contexto"])

class SolicitudComparar(BaseModel):
    actual: ContextoNegocio
    anterior: ContextoNegocio

class RespuestaComparar(BaseModel):
    similar: bool
    similitud: float

@router.post("/comparar", response_model=RespuestaComparar)
def comparar(solicitud: SolicitudComparar) -> RespuestaComparar:
    score = similitud(texto_de_contexto(solicitud.actual), texto_de_contexto(solicitud.anterior))
    return RespuestaComparar(similar=score >= UMBRAL_SIMILITUD, similitud=score)
