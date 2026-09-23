package com.iaemprender.backend.negocio.modelo;

public enum Pilar {
  EDUCATIVO("Educativo"),
  OFERTA("Oferta"),
  PRUEBA_SOCIAL("Prueba social"),
  INTERACCION("Interacción"),
  SERVICIO("Servicio");

  private final String valorDb;

  Pilar(String valorDb) {
    this.valorDb = valorDb;
  }

  public String getValorDb() {
    return valorDb;
  }

  public static Pilar desdeValorDb(String valor) {
    for (Pilar p : values()) {
      if (p.valorDb.equals(valor)) {
        return p;
      }
    }
    throw new IllegalArgumentException("Pilar no reconocido: " + valor);
  }
}
