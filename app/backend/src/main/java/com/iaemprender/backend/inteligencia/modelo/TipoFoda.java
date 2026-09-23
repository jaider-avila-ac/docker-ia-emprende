package com.iaemprender.backend.inteligencia.modelo;

public enum TipoFoda {
  FORTALEZA("Fortaleza"),
  OPORTUNIDAD("Oportunidad"),
  DEBILIDAD("Debilidad"),
  AMENAZA("Amenaza");

  private final String valorDb;

  TipoFoda(String valorDb) {
    this.valorDb = valorDb;
  }

  public String getValorDb() {
    return valorDb;
  }

  public static TipoFoda desdeValorDb(String valor) {
    for (TipoFoda t : values()) {
      if (t.valorDb.equalsIgnoreCase(valor)) {
        return t;
      }
    }
    throw new IllegalArgumentException("Tipo de FODA no reconocido: " + valor);
  }
}
