package com.iaemprender.backend.iniciativas.modelo;

public enum EstadoIniciativa {
  PROPUESTA("Propuesta"),
  EN_PRUEBA("En prueba"),
  APROBADA("Aprobada"),
  DESCARTADA("Descartada");

  private final String valorDb;

  EstadoIniciativa(String valorDb) {
    this.valorDb = valorDb;
  }

  public String getValorDb() {
    return valorDb;
  }

  public static EstadoIniciativa desdeValorDb(String valor) {
    for (EstadoIniciativa e : values()) {
      if (e.valorDb.equals(valor)) {
        return e;
      }
    }
    throw new IllegalArgumentException("Estado de iniciativa no reconocido: " + valor);
  }
}
