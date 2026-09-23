package com.iaemprender.backend.plan.modelo;

public enum TiempoDisponible {
  POCO("Poco"),
  MEDIO("Medio"),
  BASTANTE("Bastante");

  private final String valorDb;

  TiempoDisponible(String valorDb) {
    this.valorDb = valorDb;
  }

  public String getValorDb() {
    return valorDb;
  }

  public static TiempoDisponible desdeValorDb(String valor) {
    for (TiempoDisponible t : values()) {
      if (t.valorDb.equals(valor)) {
        return t;
      }
    }
    throw new IllegalArgumentException("Tiempo disponible no reconocido: " + valor);
  }
}
