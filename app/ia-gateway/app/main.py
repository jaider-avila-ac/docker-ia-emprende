from fastapi import FastAPI

from app.routers import contexto, foda, smart

app = FastAPI(
    title="IAEmprender · IA Gateway",
    description=(
        "Intermediario entre el backend Java y los proveedores de IA. "
        "Arma el prompt con el contexto del negocio, lo recorta si hace "
        "falta, le habla al proveedor real y devuelve la respuesta ya "
        "repartida en los campos que Java necesita para guardarla."
    ),
    version="0.1.0",
)

app.include_router(foda.router)
app.include_router(smart.router)
app.include_router(contexto.router)

@app.get("/salud")
def salud() -> dict:
    return {"estado": "ok"}
