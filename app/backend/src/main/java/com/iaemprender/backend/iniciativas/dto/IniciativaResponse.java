package com.iaemprender.backend.iniciativas.dto;

import com.iaemprender.backend.iniciativas.modelo.Iniciativa;
import java.util.List;

public record IniciativaResponse(
    Long id,
    String titulo,
    String metaTipo,
    String pilar,
    String estado,
    int impacto,
    int confianza,
    int esfuerzo,
    double ice,
    String descripcion,
    String notas,
    boolean generadaPorIa,
    String iaTip,
    List<Long> ofertaIds) {
  public static IniciativaResponse desde(Iniciativa i) {
    return new IniciativaResponse(
        i.getId(),
        i.getTitulo(),
        i.getMetaTipo(),
        i.getPilar(),
        i.getEstado().getValorDb(),
        i.getImpacto(),
        i.getConfianza(),
        i.getEsfuerzo(),
        calcularIce(i),
        i.getDescripcion(),
        i.getNotas(),
        i.isGeneradaPorIa(),
        i.getIaTip(),
        i.getOfertaIds().stream().sorted().toList());
  }

  private static double calcularIce(Iniciativa i) {
    if (i.getEsfuerzo() == null || i.getEsfuerzo() == 0) {
      return 0;
    }
    return Math.round((i.getImpacto() * i.getConfianza() / (double) i.getEsfuerzo()) * 10) / 10.0;
  }
}
