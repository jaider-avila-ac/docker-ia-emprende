package com.iaemprender.backend.plan.dto;

import com.iaemprender.backend.plan.modelo.PlanSemanal;
import java.util.List;

public record PlanSemanalResponse(
    Long id,
    int anio,
    int semanaNumero,
    String tiempoDisponible,
    Integer publicacionesSugeridas,
    Integer historiasSugeridas,
    String ventanaHoraria,
    List<PlanAccionResponse> acciones) {

  public static PlanSemanalResponse desde(PlanSemanal p, List<PlanAccionResponse> acciones) {
    return new PlanSemanalResponse(
        p.getId(),
        p.getAnio(),
        p.getSemanaNumero(),
        p.getTiempoDisponible().getValorDb(),
        p.getPublicacionesSugeridas(),
        p.getHistoriasSugeridas(),
        p.getVentanaHoraria(),
        acciones);
  }
}
