package com.iaemprender.backend.resultados.dto;

import com.iaemprender.backend.resultados.modelo.ResultadoSemanal;
import java.math.BigDecimal;

public record ResultadoSemanalResponse(
    Long id, int anio, int semanaNumero, BigDecimal ingresosAprox, Integer clientesAprox) {

  public static ResultadoSemanalResponse desde(ResultadoSemanal r) {
    return new ResultadoSemanalResponse(
        r.getId(), r.getAnio(), r.getSemanaNumero(), r.getIngresosAprox(), r.getClientesAprox());
  }
}
