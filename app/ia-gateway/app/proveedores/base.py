from abc import ABC, abstractmethod

class ProveedorIA(ABC):

    @abstractmethod
    def generar_json(self, prompt: str, clave_api: str) -> dict:
        raise NotImplementedError
