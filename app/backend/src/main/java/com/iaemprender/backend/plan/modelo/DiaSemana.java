package com.iaemprender.backend.plan.modelo;

public enum DiaSemana {
  LUNES("Lunes"),
  MARTES("Martes"),
  MIERCOLES("Miércoles"),
  JUEVES("Jueves"),
  VIERNES("Viernes"),
  SABADO("Sábado"),
  DOMINGO("Domingo");

  private final String valorDb;

  DiaSemana(String valorDb) {
    this.valorDb = valorDb;
  }

  public String getValorDb() {
    return valorDb;
  }

  public static DiaSemana desdeValorDb(String valor) {
    for (DiaSemana d : values()) {
      if (d.valorDb.equals(valor)) {
        return d;
      }
    }
    throw new IllegalArgumentException("Día de la semana no reconocido: " + valor);
  }
}
