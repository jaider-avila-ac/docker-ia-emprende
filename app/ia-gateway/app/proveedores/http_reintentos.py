import time

import httpx

_REINTENTABLES = {429, 500, 502, 503, 504}
_ESPERAS = (2.0, 5.0)


def post_con_reintentos(url: str, headers: dict, cuerpo: dict, timeout: float = 60.0) -> httpx.Response:
    for intento in range(len(_ESPERAS) + 1):
        ultimo = intento == len(_ESPERAS)
        try:
            respuesta = httpx.post(url, headers=headers, json=cuerpo, timeout=timeout)
        except httpx.RequestError:
            if ultimo:
                raise
        else:
            if ultimo or respuesta.status_code not in _REINTENTABLES:
                return respuesta
        time.sleep(_ESPERAS[intento])
    raise RuntimeError("inalcanzable")
