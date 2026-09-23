from app.proveedores.base import ProveedorIA

class EcoProveedor(ProveedorIA):

    def generar_json(self, prompt: str, clave_api: str) -> dict:
        return {
            "fortalezas": ["Fortaleza de prueba 1", "Fortaleza de prueba 2"],
            "oportunidades": ["Oportunidad de prueba 1"],
            "debilidades": ["Debilidad de prueba 1"],
            "amenazas": ["Amenaza de prueba 1"],
        }
