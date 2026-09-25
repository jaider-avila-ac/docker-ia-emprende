import re
from itertools import accumulate, takewhile

_NIVELES_DEL_ARBOL = (r"(?<=[.!?])\s+", r"(?<=[;:,])\s+", r"\s+")


def acortar(texto: str, maximo: int) -> str:
    completo = " ".join(texto.split())
    if len(completo) <= maximo:
        return completo
    podado = _podar(completo, maximo - 1, _NIVELES_DEL_ARBOL)
    return re.sub(r"[;:,]$", ".", podado) if podado[-1] in ".!?;:," else podado + "…"


def _podar(texto: str, presupuesto: int, niveles: tuple[str, ...]) -> str:
    if not niveles:
        return texto[:presupuesto]
    hijos = re.split(niveles[0], texto)
    prefijos = list(takewhile(lambda p: len(p) <= presupuesto, accumulate(hijos, "{} {}".format)))
    return prefijos[-1] if prefijos else _podar(hijos[0], presupuesto, niveles[1:])
