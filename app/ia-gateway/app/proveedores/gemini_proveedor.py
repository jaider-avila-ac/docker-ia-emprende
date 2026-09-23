import json

import httpx

from app.excepciones import (
    ProveedorNoDisponibleError,
    ProveedorRechazoClaveError,
    RespuestaInvalidaError,
)
from app.proveedores.base import ProveedorIA

class GeminiProveedor(ProveedorIA):
    def __init__(self, base_url: str, modelo: str = "gemini-3.5-flash"):
        self.base_url = base_url
        self.modelo = modelo

    def generar_json(self, prompt: str, clave_api: str) -> dict:
        try:
            respuesta = httpx.post(
                f"{self.base_url}/v1beta/models/{self.modelo}:generateContent",
                headers={"x-goog-api-key": clave_api},
                json={
                    "contents": [{"parts": [{"text": prompt}]}],
                    "generationConfig": {"response_mime_type": "application/json"},
                },
                timeout=60.0,
            )
        except httpx.RequestError as ex:
            raise ProveedorNoDisponibleError(f"No se pudo contactar a Gemini: {ex}") from ex

        texto_crudo = respuesta.content.decode("utf-8")

        if respuesta.status_code == 400 and "API_KEY_INVALID" in texto_crudo:
            raise ProveedorRechazoClaveError("Gemini rechazó la clave.")
        if respuesta.status_code >= 400:
            raise ProveedorNoDisponibleError(f"Gemini respondió {respuesta.status_code}: {texto_crudo[:300]}")

        try:
            cuerpo = json.loads(texto_crudo)
            contenido = cuerpo["candidates"][0]["content"]["parts"][0]["text"]
            return json.loads(contenido)
        except (KeyError, IndexError, json.JSONDecodeError) as ex:
            raise RespuestaInvalidaError(f"Respuesta de Gemini con formato inesperado: {ex}") from ex
