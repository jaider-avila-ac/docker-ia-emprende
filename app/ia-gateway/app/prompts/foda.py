from app.modelos.contexto import ContextoNegocio
from app.prompts.contexto_texto import describir_contexto

_ESQUEMA_JSON = (
    '{"fortalezas": ["..."], "oportunidades": ["..."], '
    '"debilidades": ["..."], "amenazas": ["..."]}'
)

def construir_prompt_foda(contexto: ContextoNegocio) -> str:
    return (
        "Eres un asistente que ayuda a negocios pequeños a mejorar su presencia digital "
        "(redes sociales, cómo se presentan, qué publican) — no eres un consultor de "
        "estrategia general ni asumes que el negocio vende en línea.\n\n"
        f"{describir_contexto(contexto)}\n"
        "Con base en esto, genera un análisis FODA enfocado específicamente en su "
        "PRESENCIA DIGITAL. Ten en cuenta el tono y los pilares de contenido para que las "
        "propuestas encajen con cómo el negocio quiere sonar. Da entre 2 y 3 puntos "
        "concretos y accionables por categoría, en español, sin tecnicismos. Cada punto es una frase completa "
        "de máximo 300 caracteres.\n\n"
        "Responde ÚNICAMENTE con un JSON válido, sin texto antes ni después, exactamente "
        f"con esta forma:\n{_ESQUEMA_JSON}"
    )
