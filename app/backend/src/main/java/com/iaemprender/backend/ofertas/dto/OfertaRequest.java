package com.iaemprender.backend.ofertas.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record OfertaRequest(
    @NotBlank String tipo,
    @NotBlank String nombre,
    String categoria,
    BigDecimal precio,
    String destacar) {}
