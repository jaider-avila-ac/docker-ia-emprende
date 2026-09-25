from datetime import date, timedelta

from pydantic import ValidationError

from app import config
from app.excepciones import RespuestaInvalidaError
from app.modelos.contexto import ContextoNegocio
from app.modelos.respuestas import FodaRespuesta, MetaSmartContenido, SmartRespuesta, SmartRespuestaIA
from app.prompts.foda import construir_prompt_foda
from app.prompts.smart import construir_prompt_smart
from app.proveedores.eco_proveedor import EcoProveedor
from app.proveedores.gemini_proveedor import GeminiProveedor
from app.proveedores.openai_proveedor import OpenAiProveedor
from app.servicios.optimizador import contar_tokens, recortar_contexto

_PROVEEDORES = {
    "openai": OpenAiProveedor(base_url=config.OPENAI_BASE_URL),
    "gemini": GeminiProveedor(base_url=config.GEMINI_BASE_URL),
    "deepseek": OpenAiProveedor(
        base_url=config.DEEPSEEK_BASE_URL, modelo="deepseek-chat", ruta="/chat/completions", nombre="DeepSeek"
    ),
    "eco": EcoProveedor(),
}

def _obtener_proveedor(proveedor: str):
    cliente = _PROVEEDORES.get(proveedor)
    if cliente is None:
        raise ValueError(f"Proveedor no soportado: {proveedor}")
    return cliente

def _obtener_validado(cliente, prompt: str, clave_api: str, modelo):
    try:
        return modelo.model_validate(cliente.generar_json(prompt, clave_api))
    except ValidationError as ex:
        raise RespuestaInvalidaError(f"La IA devolvió un formato inesperado: {ex.error_count()} errores") from ex


def generar_foda(proveedor: str, clave_api: str, contexto: ContextoNegocio) -> tuple[FodaRespuesta, int]:
    cliente = _obtener_proveedor(proveedor)

    contexto_recortado = recortar_contexto(contexto)
    prompt = construir_prompt_foda(contexto_recortado)
    tokens_prompt = contar_tokens(prompt)

    foda = _obtener_validado(cliente, prompt, clave_api, FodaRespuesta)
    return foda, tokens_prompt

def generar_smart(proveedor: str, clave_api: str, contexto: ContextoNegocio) -> tuple[SmartRespuesta, int]:
    cliente = _obtener_proveedor(proveedor)

    contexto_recortado = recortar_contexto(contexto)
    prompt = construir_prompt_smart(contexto_recortado)
    tokens_prompt = contar_tokens(prompt)

    smart_ia = _obtener_validado(cliente, prompt, clave_api, SmartRespuestaIA)

    hoy = date.today()
    metas = [
        MetaSmartContenido(
            titulo=meta.titulo,
            especifico=meta.especifico,
            numero_meta=meta.numero_meta,
            fecha_limite=hoy + timedelta(days=meta.dias_plazo),
            medicion=meta.medicion,
            pasos=meta.pasos,
        )
        for meta in smart_ia.metas
    ]
    return SmartRespuesta(metas=metas), tokens_prompt


def generar_con_prompt(solicitud, constructor_prompt, modelo_respuesta):
    cliente = _obtener_proveedor(solicitud.proveedor)
    contexto_recortado = recortar_contexto(solicitud.negocio)
    prompt = constructor_prompt(solicitud, contexto_recortado)
    return _obtener_validado(cliente, prompt, solicitud.clave_api, modelo_respuesta)
