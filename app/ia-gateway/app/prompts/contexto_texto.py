from app.modelos.contexto import ContextoNegocio

def describir_contexto(contexto: ContextoNegocio) -> str:
    ofertas_texto = ", ".join(contexto.ofertas) if contexto.ofertas else "no especificadas"
    pilares_texto = ", ".join(contexto.pilares) if contexto.pilares else "no definidos"

    return (
        f"Negocio: {contexto.nombre} ({contexto.rubro}).\n"
        f"Descripción: {contexto.descripcion or 'no especificada'}.\n"
        f"Público objetivo: {contexto.publico_objetivo or 'no especificado'}.\n"
        f"Diferenciador: {contexto.diferenciador or 'no especificado'}.\n"
        f"Ofertas principales: {ofertas_texto}.\n"
        f"Tono de comunicación deseado: {contexto.tono or 'no especificado'}.\n"
        f"Pilares de contenido: {pilares_texto}.\n"
    )
