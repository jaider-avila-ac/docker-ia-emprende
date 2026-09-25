package com.iaemprender.backend.inteligencia.dto;

import jakarta.validation.constraints.NotBlank;

public record FodaItemRequest(@NotBlank String contenido) {}
