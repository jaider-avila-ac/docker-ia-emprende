package com.iaemprender.backend.plan.dto;

import com.iaemprender.backend.plan.modelo.PlanAccion;

public record PlanAccionResponse(Long id, String diaSemana, String descripcion) {

  public static PlanAccionResponse desde(PlanAccion a) {
    return new PlanAccionResponse(a.getId(), a.getDiaSemana().getValorDb(), a.getDescripcion());
  }
}
