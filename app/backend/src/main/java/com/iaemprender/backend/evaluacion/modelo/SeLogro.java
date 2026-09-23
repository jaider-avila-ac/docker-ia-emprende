package com.iaemprender.backend.evaluacion.modelo;

public enum SeLogro {
  SI("Sí"),
  PARCIAL("Parcial"),
  NO("No");

  private final String valorDb;

  SeLogro(String valorDb) {
    this.valorDb = valorDb;
  }

  public String getValorDb() {
    return valorDb;
  }

  public static SeLogro desdeValorDb(String valor) {
    for (SeLogro s : values()) {
      if (s.valorDb.equals(valor)) {
        return s;
      }
    }
    throw new IllegalArgumentException("Valor de \"¿se logró?\" no reconocido: " + valor);
  }
}
