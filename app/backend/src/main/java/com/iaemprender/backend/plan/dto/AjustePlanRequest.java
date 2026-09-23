package com.iaemprender.backend.plan.dto;

import jakarta.validation.constraints.NotBlank;

public record AjustePlanRequest(@NotBlank String tiempoDisponible) {}
