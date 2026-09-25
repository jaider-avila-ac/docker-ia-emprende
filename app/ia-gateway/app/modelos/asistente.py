from pydantic import BaseModel, Field, field_validator

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
    titulo: str
    meta_tipo: str
    pilar: str | None = None
    descripcion: str
    impacto: int
    confianza: int
    esfuerzo: int
    ia_tip: str

    @field_validator("impacto", "confianza", "esfuerzo", mode="before")
    @classmethod
    def acotar(cls, valor):
        return _entre(valor, 1, 5)


class IniciativasRespuesta(BaseModel):
    iniciativas: list[IniciativaIA] = Field(min_length=1)


class AccionPlanIA(BaseModel):
    dia_semana: str
    descripcion: str


class PlanRespuesta(BaseModel):
    acciones: list[AccionPlanIA] = Field(min_length=1)


class BrandingRespuesta(BaseModel):
    tagline: str
    tono: str
    pilares: list[str] = Field(default_factory=list)
    colores: str
    referencias_estilo: str


class AnalisisRespuesta(BaseModel):
    resumen: str
    que_funciono: list[str] = Field(default_factory=list)
    que_cambiar: list[str] = Field(default_factory=list)
    siguiente_paso: str
