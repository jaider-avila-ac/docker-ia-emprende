from fastapi import APIRouter, HTTPException

from app.excepciones import (
    ProveedorNoDisponibleError,
    ProveedorRechazoClaveError,
    RespuestaInvalidaError,
)
from app.modelos.asistente import (
    AnalisisRespuesta,
    IniciativasRespuesta,
    PlanRespuesta,
    SolicitudAsistente,
)
from app.prompts.asistente import (
    construir_prompt_analisis,
    construir_prompt_iniciativas,
    construir_prompt_plan,
)
from app.servicios.orquestador import generar_con_prompt

router = APIRouter(prefix="/asistente", tags=["asistente"])


def _ejecutar(solicitud: SolicitudAsistente, constructor, modelo):
    try:
        return generar_con_prompt(solicitud, constructor, modelo)
    except ValueError as ex:
        raise HTTPException(status_code=400, detail=str(ex)) from ex
    except ProveedorRechazoClaveError as ex:
        raise HTTPException(status_code=422, detail=str(ex)) from ex
    except (ProveedorNoDisponibleError, RespuestaInvalidaError) as ex:
        raise HTTPException(status_code=502, detail=str(ex)) from ex


@router.post("/iniciativas", response_model=IniciativasRespuesta)
def iniciativas(solicitud: SolicitudAsistente) -> IniciativasRespuesta:
    return _ejecutar(solicitud, construir_prompt_iniciativas, IniciativasRespuesta)


@router.post("/plan", response_model=PlanRespuesta)
def plan(solicitud: SolicitudAsistente) -> PlanRespuesta:
    return _ejecutar(solicitud, construir_prompt_plan, PlanRespuesta)


@router.post("/analisis", response_model=AnalisisRespuesta)
def analisis(solicitud: SolicitudAsistente) -> AnalisisRespuesta:
    return _ejecutar(solicitud, construir_prompt_analisis, AnalisisRespuesta)
