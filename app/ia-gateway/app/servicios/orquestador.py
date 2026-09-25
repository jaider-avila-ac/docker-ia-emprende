from datetime import date, timedelta

from app import config
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

def generar_foda(proveedor: str, clave_api: str, contexto: ContextoNegocio) -> tuple[FodaRespuesta, int]:
    cliente = _obtener_proveedor(proveedor)

    contexto_recortado = recortar_contexto(contexto)
    prompt = construir_prompt_foda(contexto_recortado)
    tokens_prompt = contar_tokens(prompt)

    datos = cliente.generar_json(prompt, clave_api)
    foda = FodaRespuesta.model_validate(datos)
    return foda, tokens_prompt

def generar_smart(proveedor: str, clave_api: str, contexto: ContextoNegocio) -> tuple[SmartRespuesta, int]:
    cliente = _obtener_proveedor(proveedor)

    contexto_recortado = recortar_contexto(contexto)
    prompt = construir_prompt_smart(contexto_recortado)
    tokens_prompt = contar_tokens(prompt)

    datos = cliente.generar_json(prompt, clave_api)
    smart_ia = SmartRespuestaIA.model_validate(datos)

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
