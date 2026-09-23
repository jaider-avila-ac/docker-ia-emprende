from fastapi import APIRouter, HTTPException
from pydantic import BaseModel

from app.excepciones import (
    ProveedorNoDisponibleError,
    ProveedorRechazoClaveError,
    RespuestaInvalidaError,
)
from app.modelos.contexto import ContextoNegocio
from app.modelos.respuestas import FodaRespuesta
from app.servicios.orquestador import generar_foda

router = APIRouter(prefix="/foda", tags=["foda"])

class SolicitudGenerarFoda(BaseModel):
    proveedor: str
    clave_api: str
    negocio: ContextoNegocio

class RespuestaGenerarFoda(BaseModel):
    foda: FodaRespuesta
    tokens_prompt: int

@router.post("/generar", response_model=RespuestaGenerarFoda)
def generar(solicitud: SolicitudGenerarFoda) -> RespuestaGenerarFoda:
    try:
        foda, tokens = generar_foda(solicitud.proveedor, solicitud.clave_api, solicitud.negocio)
    except ValueError as ex:
        raise HTTPException(status_code=400, detail=str(ex)) from ex
    except ProveedorRechazoClaveError as ex:
        raise HTTPException(status_code=422, detail=str(ex)) from ex
    except (ProveedorNoDisponibleError, RespuestaInvalidaError) as ex:
        raise HTTPException(status_code=502, detail=str(ex)) from ex

    return RespuestaGenerarFoda(foda=foda, tokens_prompt=tokens)
