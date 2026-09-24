package com.iaemprender.backend.inteligencia.dto;

import com.iaemprender.backend.inteligencia.modelo.MetaSmart;
import java.time.LocalDate;

public record MetaSmartResponse(
    Long id,
    String titulo,
    String especifico,
    String numeroMeta,
    LocalDate fechaLimite,
    String medicion,
    String pasos,
    boolean generadoPorIa) {
  public static MetaSmartResponse desde(MetaSmart m) {
    return new MetaSmartResponse(
        m.getId(),
        m.getTitulo(),
        m.getEspecifico(),
        m.getNumeroMeta(),
        m.getFechaLimite(),
        m.getMedicion(),
        m.getPasos(),
        m.isGeneradoPorIa());
  }
}
