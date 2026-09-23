package com.iaemprender.backend.inteligencia.dto;

import com.iaemprender.backend.inteligencia.modelo.FodaItem;

public record FodaItemResponse(Long id, String tipo, String contenido, boolean generadoPorIa) {

  public static FodaItemResponse desde(FodaItem f) {
    return new FodaItemResponse(f.getId(), f.getTipo().getValorDb(), f.getContenido(), f.isGeneradoPorIa());
  }
}
