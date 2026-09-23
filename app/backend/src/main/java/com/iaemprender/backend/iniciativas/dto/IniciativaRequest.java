package com.iaemprender.backend.iniciativas.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record IniciativaRequest(
    @NotBlank String titulo,
    String metaTipo,
    String pilar,
    String estado,
    @NotNull @Min(1) @Max(5) Integer impacto,
    @NotNull @Min(1) @Max(5) Integer confianza,
    @NotNull @Min(1) @Max(5) Integer esfuerzo,
    String descripcion,
    String notas,
    List<Long> ofertaIds) {}
