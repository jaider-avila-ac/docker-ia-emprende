import json

import httpx

from app.excepciones import (
    ProveedorNoDisponibleError,
    ProveedorRechazoClaveError,
    RespuestaInvalidaError,
)
from app.proveedores.base import ProveedorIA

class OpenAiProveedor(ProveedorIA):
    def __init__(self, base_url: str, modelo: str = "gpt-4o-mini"):
        self.base_url = base_url
        self.modelo = modelo

    def generar_json(self, prompt: str, clave_api: str) -> dict:
        try:
            respuesta = httpx.post(
                f"{self.base_url}/v1/chat/completions",
                headers={"Authorization": f"Bearer {clave_api}"},
                json={
                    "model": self.modelo,
                    "messages": [{"role": "user", "content": prompt}],
                    "response_format": {"type": "json_object"},
                },
                timeout=60.0,
            )
        except httpx.RequestError as ex:
            raise ProveedorNoDisponibleError(f"No se pudo contactar a OpenAI: {ex}") from ex

        texto_crudo = respuesta.content.decode("utf-8")

        if respuesta.status_code == 401:
            raise ProveedorRechazoClaveError("OpenAI rechazó la clave.")
        if respuesta.status_code >= 400:
            raise ProveedorNoDisponibleError(f"OpenAI respondió {respuesta.status_code}: {texto_crudo[:300]}")

        try:
            cuerpo = json.loads(texto_crudo)
            contenido = cuerpo["choices"][0]["message"]["content"]
            return json.loads(contenido)
        except (KeyError, IndexError, json.JSONDecodeError) as ex:
            raise RespuestaInvalidaError(f"Respuesta de OpenAI con formato inesperado: {ex}") from ex
