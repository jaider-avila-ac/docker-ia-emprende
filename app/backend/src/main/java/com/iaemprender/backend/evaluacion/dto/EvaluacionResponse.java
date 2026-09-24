package com.iaemprender.backend.evaluacion.dto;

import com.iaemprender.backend.evaluacion.modelo.Evaluacion;
import java.time.LocalDateTime;

public record EvaluacionResponse(
    Long id,
    Long iniciativaId,
    Long planSemanalId,
    String seLogro,
    String dificultad,
    String repetiria,
    String comentarios,
    LocalDateTime creadoEn) {
  public static EvaluacionResponse desde(Evaluacion e) {
    return new EvaluacionResponse(
        e.getId(),
        e.getIniciativaId(),
        e.getPlanSemanalId(),
        e.getSeLogro().getValorDb(),
        e.getDificultad().getValorDb(),
        e.getRepetiria().getValorDb(),
        e.getComentarios(),
        e.getCreadoEn());
  }
}
