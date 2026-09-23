package com.iaemprender.backend.competidores.dto;

import jakarta.validation.constraints.NotBlank;

public record CompetidorRequest(@NotBlank String nombre, String canal, String notas) {}
