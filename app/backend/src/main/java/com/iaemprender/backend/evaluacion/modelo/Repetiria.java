package com.iaemprender.backend.evaluacion.modelo;

public enum Repetiria {
  SI("Sí"),
  CON_CAMBIOS("Con cambios"),
  NO("No");

  private final String valorDb;

  Repetiria(String valorDb) {
    this.valorDb = valorDb;
  }

  public String getValorDb() {
    return valorDb;
  }

  public static Repetiria desdeValorDb(String valor) {
    for (Repetiria r : values()) {
      if (r.valorDb.equals(valor)) {
        return r;
      }
    }
    throw new IllegalArgumentException("Valor de \"¿repetirías?\" no reconocido: " + valor);
  }
}
