import json
import logging

from pydantic import BaseModel, ValidationError

from app.excepciones import RespuestaInvalidaError
from app.servicios.texto import acortar

registro = logging.getLogger("uvicorn.error")

MAX_REPARACIONES = 2


def describir_errores(ex: ValidationError) -> str:
    lineas = []
    for error in ex.errors():
        ruta = ".".join(str(parte) for parte in error["loc"]) or "respuesta"
        tipo = error["type"]
        contexto = error.get("ctx") or {}
        if tipo == "string_too_long":
            lineas.append(
                f"- {ruta}: tiene {len(str(error['input']))} caracteres y el máximo es {contexto['max_length']}"
            )
        elif tipo == "string_too_short":
            lineas.append(f"- {ruta}: está vacío")
        elif tipo == "too_short":
            lineas.append(f"- {ruta}: faltan elementos (mínimo {contexto['min_length']})")
        elif tipo == "missing":
            lineas.append(f"- {ruta}: falta este campo")
        else:
            lineas.append(f"- {ruta}: {error['msg']}")
    return "\n".join(lineas)


def prompt_reparacion(prompt_original: str, respuesta, ex: ValidationError) -> str:
    return (
        f"{prompt_original}\n\n---\n"
        f"Tu respuesta anterior fue:\n{json.dumps(respuesta, ensure_ascii=False)}\n\n"
        f"No se puede usar tal cual por estos problemas:\n{describir_errores(ex)}\n\n"
        "Corrígela: vuelve a escribir solo los textos con problemas, acortándolos con tus propias palabras en "
        "frases completas y con sentido (nunca cortadas a la mitad ni con puntos suspensivos), respeta los valores "
        "permitidos y devuelve el JSON completo con la misma estructura."
    )


def acortar_textos_largos(datos, ex: ValidationError) -> bool:
    cambio = False
    for error in ex.errors():
        if error["type"] != "string_too_long":
            continue
        destino = datos
        try:
            for parte in error["loc"][:-1]:
                destino = destino[parte]
            clave = error["loc"][-1]
            destino[clave] = acortar(str(destino[clave]), error["ctx"]["max_length"])
            cambio = True
        except (KeyError, IndexError, TypeError):
            continue
    return cambio


def obtener_validado(cliente, prompt: str, clave_api: str, modelo: type[BaseModel]):
    datos = cliente.generar_json(prompt, clave_api)
    for intento in range(MAX_REPARACIONES + 1):
        try:
            return modelo.model_validate(datos)
        except ValidationError as ex:
            if intento < MAX_REPARACIONES:
                registro.info(
                    "Pidiendo a la IA que corrija su respuesta (intento %s): %s",
                    intento + 1,
                    describir_errores(ex)[:200],
                )
                datos = cliente.generar_json(prompt_reparacion(prompt, datos, ex), clave_api)
                continue

            if acortar_textos_largos(datos, ex):
                registro.info("La IA no acortó sus textos; se acortan por frases completas.")
                try:
                    return modelo.model_validate(datos)
                except ValidationError as ex2:
                    ex = ex2
            raise RespuestaInvalidaError(
                f"La IA no logró cumplir el formato tras {MAX_REPARACIONES} correcciones: {describir_errores(ex)[:300]}"
            ) from ex
