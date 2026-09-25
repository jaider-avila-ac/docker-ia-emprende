from app.modelos.asistente import SolicitudAsistente
from app.prompts.contexto_texto import describir_contexto

_ROL = (
    "Eres un asistente que ayuda a negocios pequeños a mejorar su presencia digital "
    "(redes sociales, cómo se presentan, qué publican). No asumes que el negocio vende en "
    "línea ni das consejos de estrategia general. Escribes en español, claro y sin tecnicismos.\n\n"
)

_SOLO_JSON = "Responde ÚNICAMENTE con un JSON válido, sin texto antes ni después, con esta forma:\n"


def _lista(titulo: str, elementos: list[str], vacio: str) -> str:
    if not elementos:
        return f"{titulo}: {vacio}.\n"
    lineas = "\n".join(f"- {e}" for e in elementos)
    return f"{titulo}:\n{lineas}\n"


def _foda_texto(foda: dict[str, list[str]]) -> str:
    if not any(foda.values()):
        return "FODA: todavía no generado.\n"
    partes = []
    for clave, etiqueta in (
        ("fortalezas", "Fortalezas"),
        ("oportunidades", "Oportunidades"),
        ("debilidades", "Debilidades"),
        ("amenazas", "Amenazas"),
    ):
        items = foda.get(clave) or []
        if items:
            partes.append(f"{etiqueta}: " + "; ".join(items))
    return "FODA:\n" + "\n".join(partes) + "\n"


def construir_prompt_iniciativas(s: SolicitudAsistente, contexto) -> str:
    esquema = (
        '{"iniciativas": [{"titulo": "...", "meta_tipo": "Constancia|Presentación|Interacción|Alcance", '
        '"pilar": "Educativo|Oferta|Prueba social|Interacción|Servicio", "descripcion": "...", '
        '"impacto": 1-5, "confianza": 1-5, "esfuerzo": 1-5, "ia_tip": "..."}, ... exactamente 5 objetos así]}'
    )
    return (
        _ROL
        + f"{describir_contexto(contexto)}\n"
        + _foda_texto(s.foda)
        + _lista("Metas SMART del negocio", s.metas, "todavía no generadas")
        + _lista("Aprendizajes de iniciativas ya probadas (aprende de esto)", s.aprendizajes, "ninguno todavía")
        + _lista("Resultados recientes", s.resultados, "sin registrar")
        + _lista("Iniciativas que ya existen (no las repitas)", s.iniciativas, "ninguna")
        + "\nPropón exactamente 5 iniciativas nuevas de presencia digital, concretas y realizables por un "
        "negocio pequeño, que aprovechen las fortalezas y oportunidades del FODA, ayuden a cumplir las metas "
        "SMART y repitan lo que funcionó según los aprendizajes (evita lo que salió mal). 'titulo' es una acción "
        "clara (ej. 'Video corto mostrando cómo preparan el producto'). 'descripcion' explica en una o dos "
        "frases qué hacer y por qué. 'impacto', 'confianza' y 'esfuerzo' son enteros de 1 a 5. "
        "'ia_tip' es un consejo breve para ejecutarla mejor.\n\n"
        + _SOLO_JSON
        + esquema
    )


def construir_prompt_plan(s: SolicitudAsistente, contexto) -> str:
    esquema = '{"acciones": [{"dia_semana": "Lunes", "descripcion": "..."}, ... entre 5 y 9 objetos]}'
    plan = s.plan
    ajustes = (
        f"Tiempo disponible del dueño esta semana: {plan.tiempo_disponible}. Publicaciones sugeridas: "
        f"{plan.publicaciones}. Historias sugeridas: {plan.historias}. Ventana horaria: {plan.ventana}.\n"
        if plan
        else ""
    )
    return (
        _ROL
        + f"{describir_contexto(contexto)}\n"
        + _lista("Iniciativas priorizadas para esta semana (de mayor a menor prioridad)", s.iniciativas, "ninguna")
        + _lista("Aprendizajes de semanas anteriores", s.aprendizajes, "ninguno todavía")
        + ajustes
        + "\nArma el plan de acciones de esta semana. Cada acción es una tarea concreta para un día, en una "
        "frase corta (máximo 200 caracteres), y debe salir de las iniciativas priorizadas. Reparte el trabajo "
        "en varios días, sin saturar ninguno, respetando el tiempo disponible y las publicaciones/historias "
        "sugeridas. 'dia_semana' debe ser exactamente uno de: Lunes, Martes, Miércoles, Jueves, Viernes, "
        "Sábado, Domingo.\n\n"
        + _SOLO_JSON
        + esquema
    )


def construir_prompt_branding(s: SolicitudAsistente, contexto) -> str:
    esquema = (
        '{"tagline": "...", "tono": "...", "pilares": ["Educativo|Oferta|Prueba social|Interacción|Servicio", '
        '"... entre 2 y 4"], "colores": "...", "referencias_estilo": "..."}'
    )
    return (
        _ROL
        + f"{describir_contexto(contexto)}\n"
        + _foda_texto(s.foda)
        + "\nPropón la identidad de marca del negocio para sus redes. 'tagline' es una frase corta y memorable "
        "(máximo 80 caracteres). 'tono' describe cómo debe hablar el negocio (máximo 100 caracteres). 'pilares' "
        "son entre 2 y 4 de los valores permitidos, los que mejor le convienen. 'colores' sugiere una paleta "
        "simple con nombres de colores (máximo 120 caracteres). 'referencias_estilo' describe el estilo "
        "visual y de fotografía recomendado (máximo 200 caracteres).\n\n"
        + _SOLO_JSON
        + esquema
    )


def construir_prompt_analisis(s: SolicitudAsistente, contexto) -> str:
    esquema = (
        '{"resumen": "...", "que_funciono": ["..."], "que_cambiar": ["..."], "siguiente_paso": "..."}'
    )
    return (
        _ROL
        + f"{describir_contexto(contexto)}\n"
        + _lista("Iniciativas evaluadas por el dueño", s.aprendizajes, "ninguna")
        + _lista("Resultados semanales registrados", s.resultados, "sin registrar")
        + _lista("Metas SMART", s.metas, "no generadas")
        + "\nAnaliza cómo le está yendo al negocio con su presencia digital a partir de las evaluaciones y los "
        "resultados. 'resumen' son dos o tres frases con la lectura general. 'que_funciono' y 'que_cambiar' son "
        "listas de 2 a 4 puntos concretos y accionables. 'siguiente_paso' es la única acción más importante "
        "para la próxima semana.\n\n"
        + _SOLO_JSON
        + esquema
    )
