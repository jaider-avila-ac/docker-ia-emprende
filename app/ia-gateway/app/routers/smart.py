from fastapi import APIRouter, HTTPException
from pydantic import BaseModel

from app.excepciones import (
    ProveedorNoDisponibleError,
    ProveedorRechazoClaveError,
    RespuestaInvalidaError,
)
from app.modelos.contexto import ContextoNegocio
from app.modelos.respuestas import SmartRespuesta
from app.servicios.orquestador import generar_smart

router = APIRouter(prefix="/smart", tags=["smart"])

class SolicitudGenerarSmart(BaseModel):
    proveedor: str
    clave_api: str
    negocio: ContextoNegocio

class RespuestaGenerarSmart(BaseModel):
    smart: SmartRespuesta
    tokens_prompt: int

@router.post("/generar", response_model=RespuestaGenerarSmart)
def generar(solicitud: SolicitudGenerarSmart) -> RespuestaGenerarSmart:
    try:
        smart, tokens = generar_smart(solicitud.proveedor, solicitud.clave_api, solicitud.negocio)
    except ValueError as ex:
        raise HTTPException(status_code=400, detail=str(ex)) from ex
    except ProveedorRechazoClaveError as ex:
        raise HTTPException(status_code=422, detail=str(ex)) from ex
    except (ProveedorNoDisponibleError, RespuestaInvalidaError) as ex:
        raise HTTPException(status_code=502, detail=str(ex)) from ex

    return RespuestaGenerarSmart(smart=smart, tokens_prompt=tokens)
