from datetime import date
from typing import Annotated

from pydantic import BaseModel, Field, field_validator

from app.modelos import limites

TextoFoda = Annotated[str, Field(min_length=1, max_length=limites.FODA_ITEM)]


class FodaRespuesta(BaseModel):
    fortalezas: list[TextoFoda] = Field(min_length=1)
    oportunidades: list[TextoFoda] = Field(min_length=1)
    debilidades: list[TextoFoda] = Field(min_length=1)
    amenazas: list[TextoFoda] = Field(min_length=1)


class MetaSmartIA(BaseModel):
    titulo: str = Field(min_length=1, max_length=limites.SMART_TITULO)
    especifico: str = Field(max_length=limites.SMART_ESPECIFICO)
    numero_meta: str = Field(max_length=limites.SMART_NUMERO_META)
    dias_plazo: int
    medicion: str = Field(max_length=limites.SMART_MEDICION)
    pasos: str = Field(max_length=limites.SMART_PASOS)

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
