from datetime import date

from pydantic import BaseModel, Field

class FodaRespuesta(BaseModel):

    fortalezas: list[str] = Field(min_length=1)
    oportunidades: list[str] = Field(min_length=1)
    debilidades: list[str] = Field(min_length=1)
    amenazas: list[str] = Field(min_length=1)

class MetaSmartIA(BaseModel):

    titulo: str
    especifico: str
    numero_meta: str
    dias_plazo: int = Field(ge=7, le=90)
    medicion: str
    pasos: str

class SmartRespuestaIA(BaseModel):
    metas: list[MetaSmartIA] = Field(min_length=3, max_length=3)

class MetaSmartContenido(BaseModel):

    titulo: str
    especifico: str
    numero_meta: str
    fecha_limite: date
    medicion: str
    pasos: str

class SmartRespuesta(BaseModel):
    metas: list[MetaSmartContenido]
