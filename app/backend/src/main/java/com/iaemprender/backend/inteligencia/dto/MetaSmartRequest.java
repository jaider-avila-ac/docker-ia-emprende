package com.iaemprender.backend.inteligencia.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record MetaSmartRequest(
    @NotBlank String titulo,
    String especifico,
    String numeroMeta,
    LocalDate fechaLimite,
    String medicion,
    String pasos) {}
