package com.iaemprender.backend.ofertas.modelo;

public enum TipoOferta {
  PRODUCTO("Producto"),
  SERVICIO("Servicio");

  private final String valorDb;

  TipoOferta(String valorDb) {
    this.valorDb = valorDb;
  }

  public String getValorDb() {
    return valorDb;
  }

  public static TipoOferta desdeValorDb(String valor) {
    for (TipoOferta t : values()) {
      if (t.valorDb.equalsIgnoreCase(valor)) {
        return t;
      }
    }
    throw new IllegalArgumentException("Tipo de oferta no reconocido: " + valor);
  }
}
