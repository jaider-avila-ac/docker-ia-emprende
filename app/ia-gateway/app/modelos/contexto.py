from pydantic import BaseModel, Field

class ContextoNegocio(BaseModel):

    nombre: str
    rubro: str
    descripcion: str | None = None
    publico_objetivo: str | None = None
    diferenciador: str | None = None
    ofertas: list[str] = Field(default_factory=list)
    tagline: str | None = None
    tono: str | None = None
    pilares: list[str] = Field(default_factory=list)
    competidores: list[str] = Field(default_factory=list)
