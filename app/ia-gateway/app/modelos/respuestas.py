from datetime import date

from pydantic import BaseModel, Field, field_validator

from app.modelos import limites
from app.modelos.tipos import texto


class FodaRespuesta(BaseModel):
    fortalezas: list[texto(limites.FODA_ITEM, 1)] = Field(min_length=1)
    oportunidades: list[texto(limites.FODA_ITEM, 1)] = Field(min_length=1)
    debilidades: list[texto(limites.FODA_ITEM, 1)] = Field(min_length=1)
    amenazas: list[texto(limites.FODA_ITEM, 1)] = Field(min_length=1)


class MetaSmartIA(BaseModel):
    titulo: texto(limites.SMART_TITULO, 1)
    especifico: texto(limites.SMART_ESPECIFICO)
    numero_meta: texto(limites.SMART_NUMERO_META)
    dias_plazo: int
    medicion: texto(limites.SMART_MEDICION)
    pasos: texto(limites.SMART_PASOS)

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
