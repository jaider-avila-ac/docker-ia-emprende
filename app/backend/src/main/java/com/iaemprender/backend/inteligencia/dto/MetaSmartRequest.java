package com.iaemprender.backend.inteligencia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record MetaSmartRequest(
    @NotBlank @Size(max = 255) String titulo,
    String especifico,
    @Size(max = 255) String numeroMeta,
    LocalDate fechaLimite,
    String medicion,
    String pasos) {}
