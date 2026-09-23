package com.iaemprender.backend.apikeys.dto;

import jakarta.validation.constraints.NotBlank;

public record GuardarApiKeyRequest(@NotBlank String clave) {}
