package com.iaemprender.backend.plan.dto;

import jakarta.validation.constraints.NotBlank;

public record PlanAccionRequest(@NotBlank String diaSemana, @NotBlank String descripcion) {}
