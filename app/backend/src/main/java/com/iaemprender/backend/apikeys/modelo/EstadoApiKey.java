package com.iaemprender.backend.apikeys.modelo;

public enum EstadoApiKey {
  ACTIVA("activa"),
  INVALIDA("invalida"),
  DESACTIVADA("desactivada");

  private final String valorDb;

  EstadoApiKey(String valorDb) {
    this.valorDb = valorDb;
  }

  public String getValorDb() {
    return valorDb;
  }

  public static EstadoApiKey desdeValorDb(String valor) {
    for (EstadoApiKey e : values()) {
      if (e.valorDb.equalsIgnoreCase(valor)) {
        return e;
      }
    }
    throw new IllegalArgumentException("Estado de API key no reconocido: " + valor);
  }
}
