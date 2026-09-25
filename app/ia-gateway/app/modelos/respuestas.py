from datetime import date

from pydantic import BaseModel, Field, field_validator

class FodaRespuesta(BaseModel):

    fortalezas: list[str] = Field(min_length=1)
    oportunidades: list[str] = Field(min_length=1)
    debilidades: list[str] = Field(min_length=1)
    amenazas: list[str] = Field(min_length=1)

class MetaSmartIA(BaseModel):

    titulo: str
    especifico: str
    numero_meta: str
    dias_plazo: int
    medicion: str
    pasos: str

    @field_validator("dias_plazo", mode="before")
    @classmethod
    def acotar_plazo(cls, valor):
        try:
            numero = int(round(float(valor)))
        except (TypeError, ValueError):
            return 30
        return max(7, min(90, numero))

class SmartRespuestaIA(BaseModel):
    metas: list[MetaSmartIA] = Field(min_length=3)

class MetaSmartContenido(BaseModel):

    titulo: str
    especifico: str
    numero_meta: str
    fecha_limite: date
    medicion: str
    pasos: str

class SmartRespuesta(BaseModel):
    metas: list[MetaSmartContenido]
