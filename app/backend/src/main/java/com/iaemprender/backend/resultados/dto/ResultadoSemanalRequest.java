package com.iaemprender.backend.resultados.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ResultadoSemanalRequest(
    @NotNull Integer anio,
    @NotNull Integer semanaNumero,
    @NotNull BigDecimal ingresosAprox,
    Integer clientesAprox) {}
