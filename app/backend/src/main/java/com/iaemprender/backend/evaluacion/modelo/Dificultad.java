package com.iaemprender.backend.evaluacion.modelo;

public enum Dificultad {
  BAJA("Baja"),
  MEDIA("Media"),
  ALTA("Alta");

  private final String valorDb;

  Dificultad(String valorDb) {
    this.valorDb = valorDb;
  }

  public String getValorDb() {
    return valorDb;
  }

  public static Dificultad desdeValorDb(String valor) {
    for (Dificultad d : values()) {
      if (d.valorDb.equals(valor)) {
        return d;
      }
    }
    throw new IllegalArgumentException("Dificultad no reconocida: " + valor);
  }
}
