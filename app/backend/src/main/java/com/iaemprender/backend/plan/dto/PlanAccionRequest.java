package com.iaemprender.backend.plan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PlanAccionRequest(@NotBlank String diaSemana, @NotBlank @Size(max = 255) String descripcion) {}
