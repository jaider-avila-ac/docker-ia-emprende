package com.iaemprender.backend.negocio.modelo;

public enum Rubro {
  RESTAURANTE("Restaurante"),
  PELUQUERIA_ESTETICA("Peluquería / estética"),
  CONSULTORIO_SERVICIO_PROFESIONAL("Consultorio / servicio profesional"),
  TIENDA_FISICA("Tienda física"),
  SOFTWARE_SAAS("Software como servicio (SaaS)"),
  APP_MOVIL_SOFTWARE("Aplicación móvil / software a medida"),
  AGENCIA_DIGITAL("Agencia digital / marketing"),
  ECOMMERCE("Tienda en línea / e-commerce"),
  EDUCACION_CURSOS("Educación / cursos en línea"),
  CONSULTOR_FREELANCER("Consultor / freelancer independiente"),
  SALUD_BIENESTAR("Salud y bienestar / gimnasio"),
  TURISMO_HOSPEDAJE("Turismo / hospedaje"),
  FOTOGRAFIA_AUDIOVISUAL("Fotografía / producción audiovisual"),
  OTRO("Otro");

  private final String valorDb;

  Rubro(String valorDb) {
    this.valorDb = valorDb;
  }

  public String getValorDb() {
    return valorDb;
  }

  public static Rubro desdeValorDb(String valor) {
    for (Rubro r : values()) {
      if (r.valorDb.equals(valor)) {
        return r;
      }
    }
    throw new IllegalArgumentException("Rubro no reconocido: " + valor);
  }
}
