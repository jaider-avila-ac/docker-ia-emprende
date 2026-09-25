# IAEmprender

Aplicacion web para que un emprendedor registre los datos de su negocio y reciba, a partir de ellos, un FODA, metas SMART, iniciativas priorizadas (ICE) y un plan semanal generados con IA.

## Servicios

- **frontend**: React, Vite y Tailwind, servido con nginx.
- **backend**: Spring Boot, JWT y Flyway.
- **ia-gateway**: FastAPI que llama a OpenAI, Gemini o DeepSeek y guarda un cache semantico.
- **mysql**: base de datos.

## Como correrlo

Solo necesitas Docker.

```bash
cd app
docker compose up -d --build
```

- Aplicacion: http://localhost:5173
- API: http://localhost:8080
- Adminer: http://localhost:8081

El gateway de IA no expone puerto, solo lo usa el backend por la red interna de Docker.

## Uso

1. Crea una cuenta en `/registro` e inicia sesion.
2. Crea tu negocio y completa su perfil, ofertas y resultados.
3. En Configuracion pega tu clave de OpenAI, Gemini o DeepSeek.
4. En Inteligencia genera el FODA y las metas SMART, y de ahi las iniciativas y el plan semanal. La IA genera y tu editas o dejas tal cual.

Las claves de IA se guardan cifradas en la base de datos.
