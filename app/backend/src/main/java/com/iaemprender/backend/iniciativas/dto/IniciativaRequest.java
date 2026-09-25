package com.iaemprender.backend.iniciativas.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record IniciativaRequest(
    @NotBlank @Size(max = 255) String titulo,
    @Size(max = 100) String metaTipo,
    @Size(max = 100) String pilar,
    String estado,
    @NotNull @Min(1) @Max(5) Integer impacto,
    @NotNull @Min(1) @Max(5) Integer confianza,
    @NotNull @Min(1) @Max(5) Integer esfuerzo,
    String descripcion,
    String notas,
    List<Long> ofertaIds) {}
