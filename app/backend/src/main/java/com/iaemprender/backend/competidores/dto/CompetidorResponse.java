package com.iaemprender.backend.competidores.dto;

import com.iaemprender.backend.competidores.modelo.Competidor;

public record CompetidorResponse(Long id, String nombre, String canal, String notas) {

  public static CompetidorResponse desde(Competidor c) {
    return new CompetidorResponse(c.getId(), c.getNombre(), c.getCanal(), c.getNotas());
  }
}
