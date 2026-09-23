package com.iaemprender.backend.negocio.modelo;

public enum Rubro {
  RESTAURANTE("Restaurante"),
  PELUQUERIA_ESTETICA("Peluquería / estética"),
  CONSULTORIO_SERVICIO_PROFESIONAL("Consultorio / servicio profesional"),
  TIENDA_FISICA("Tienda física"),
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
