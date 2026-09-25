import json

import httpx

from app.excepciones import (
    ProveedorNoDisponibleError,
    ProveedorRechazoClaveError,
    RespuestaInvalidaError,
)
from app.proveedores.base import ProveedorIA
from app.proveedores.http_reintentos import post_con_reintentos

class OpenAiProveedor(ProveedorIA):
    def __init__(self, base_url: str, modelo: str = "gpt-4o-mini", ruta: str = "/v1/chat/completions", nombre: str = "OpenAI"):
        self.base_url = base_url
        self.modelo = modelo
        self.ruta = ruta
        self.nombre = nombre

    def generar_json(self, prompt: str, clave_api: str) -> dict:
        try:
            respuesta = post_con_reintentos(
                f"{self.base_url}{self.ruta}",
                {"Authorization": f"Bearer {clave_api}"},
                {
                    "model": self.modelo,
                    "messages": [{"role": "user", "content": prompt}],
                    "response_format": {"type": "json_object"},
                },
            )
        except httpx.RequestError as ex:
            raise ProveedorNoDisponibleError(f"No se pudo contactar a {self.nombre}: {ex}") from ex

        texto_crudo = respuesta.content.decode("utf-8")

        if respuesta.status_code == 401:
            raise ProveedorRechazoClaveError(f"{self.nombre} rechazó la clave.")
        if respuesta.status_code >= 400:
            raise ProveedorNoDisponibleError(f"{self.nombre} respondió {respuesta.status_code}: {texto_crudo[:300]}")

        try:
            cuerpo = json.loads(texto_crudo)
            contenido = cuerpo["choices"][0]["message"]["content"]
            return json.loads(contenido)
        except (KeyError, IndexError, json.JSONDecodeError) as ex:
            raise RespuestaInvalidaError(f"Respuesta de {self.nombre} con formato inesperado: {ex}") from ex
