package com.iaemprender.backend.usuario.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(@NotBlank String correo, @NotBlank String password) {}
