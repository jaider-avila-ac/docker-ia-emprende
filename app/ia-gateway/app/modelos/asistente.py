from typing import Annotated

from pydantic import BaseModel, Field, field_validator

from app.modelos import limites
from app.modelos.contexto import ContextoNegocio


def _entre(valor, minimo, maximo):
    try:
        numero = int(round(float(valor)))
    except (TypeError, ValueError):
        return minimo
    return max(minimo, min(maximo, numero))


class DatosPlan(BaseModel):
    tiempo_disponible: str
    publicaciones: int
    historias: int
    ventana: str


class SolicitudAsistente(BaseModel):
    proveedor: str
    clave_api: str
    negocio: ContextoNegocio
    foda: dict[str, list[str]] = Field(default_factory=dict)
    metas: list[str] = Field(default_factory=list)
    aprendizajes: list[str] = Field(default_factory=list)
    resultados: list[str] = Field(default_factory=list)
    iniciativas: list[str] = Field(default_factory=list)
    plan: DatosPlan | None = None


class IniciativaIA(BaseModel):
    titulo: str = Field(min_length=1, max_length=limites.INICIATIVA_TITULO)
    meta_tipo: str | None = None
    pilar: str | None = None
    descripcion: str = Field(max_length=limites.INICIATIVA_DESCRIPCION)
    impacto: int
    confianza: int
    esfuerzo: int
    ia_tip: str = Field(max_length=limites.INICIATIVA_TIP)

    @field_validator("impacto", "confianza", "esfuerzo", mode="before")
    @classmethod
    def acotar(cls, valor):
        return _entre(valor, 1, 5)

    @field_validator("meta_tipo", mode="after")
    @classmethod
    def normalizar_meta_tipo(cls, valor):
        return limites.canonico(valor, limites.META_TIPOS)

    @field_validator("pilar", mode="after")
    @classmethod
    def normalizar_pilar(cls, valor):
        return limites.canonico(valor, limites.PILARES)


class IniciativasRespuesta(BaseModel):
    iniciativas: list[IniciativaIA] = Field(min_length=1)


class AccionPlanIA(BaseModel):
    dia_semana: str
    descripcion: str = Field(min_length=1, max_length=limites.ACCION_PLAN)

    @field_validator("dia_semana", mode="after")
    @classmethod
    def normalizar_dia(cls, valor):
        return limites.canonico(valor, limites.DIAS)


class PlanRespuesta(BaseModel):
    acciones: list[AccionPlanIA] = Field(min_length=1)

    @field_validator("acciones", mode="before")
    @classmethod
    def descartar_dias_inexistentes(cls, acciones):
        return [a for a in acciones if isinstance(a, dict) and limites.canonico(a.get("dia_semana"), limites.DIAS)]


PuntoAnalisis = Annotated[str, Field(min_length=1, max_length=limites.ANALISIS_ITEM)]


class AnalisisRespuesta(BaseModel):
    resumen: str = Field(min_length=1, max_length=limites.ANALISIS_RESUMEN)
    que_funciono: list[PuntoAnalisis] = Field(default_factory=list)
    que_cambiar: list[PuntoAnalisis] = Field(default_factory=list)
    siguiente_paso: str = Field(min_length=1, max_length=limites.ANALISIS_PASO)
