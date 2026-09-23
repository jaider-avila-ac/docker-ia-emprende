package com.iaemprender.backend.evaluacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EvaluacionRequest(
    @NotNull Long iniciativaId,
    Integer semanaNumero,
    @NotBlank String seLogro,
    @NotBlank String dificultad,
    @NotBlank String repetiria,
    String comentarios) {}
