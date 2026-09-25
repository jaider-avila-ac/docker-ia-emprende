from app.modelos.contexto import ContextoNegocio
from app.prompts.contexto_texto import describir_contexto

_ESQUEMA_JSON = (
    '{"metas": [{"titulo": "...", "especifico": "...", "numero_meta": "...", '
    '"dias_plazo": 30, "medicion": "...", "pasos": "..."}, '
    "... exactamente 3 objetos así]}"
)

def construir_prompt_smart(contexto: ContextoNegocio) -> str:
    return (
        "Eres un asistente que ayuda a negocios pequeños a mejorar su presencia digital "
        "(redes sociales, cómo se presentan, qué publican) — no eres un consultor de "
        "estrategia general ni asumes que el negocio vende en línea.\n\n"
        f"{describir_contexto(contexto)}\n"
        "Con base en esto, genera exactamente 3 metas SMART (específicas, medibles, "
        "alcanzables, relevantes y con plazo) enfocadas en su PRESENCIA DIGITAL: una sobre "
        "constancia de publicación, una sobre cómo se presenta/lo encuentran (perfil, bio, "
        "ficha de Google, fotos), y una sobre cómo responde e interactúa con la gente. "
        "Cada meta debe ser concreta y alcanzable para un negocio pequeño, en español, sin "
        "tecnicismos. 'dias_plazo' debe ser un número entero razonable entre 7 y 90.\n"
        "Cada texto debe ser una frase completa y concisa que quepa en su límite de caracteres (nunca la dejes "
        "cortada): 'titulo' máximo 150, 'especifico' máximo 400, 'numero_meta' máximo 150, 'medicion' máximo 300 "
        "y 'pasos' máximo 500.\n\n"
        "Responde ÚNICAMENTE con un JSON válido, sin texto antes ni después, exactamente "
        f"con esta forma:\n{_ESQUEMA_JSON}"
    )
