from typing import Annotated

from pydantic import BeforeValidator, Field

from app.servicios.texto import acortar


def texto(maximo: int, minimo: int = 0):
    return Annotated[
        str,
        BeforeValidator(lambda valor: acortar(valor, maximo) if isinstance(valor, str) else valor),
        Field(min_length=minimo, max_length=maximo),
    ]
