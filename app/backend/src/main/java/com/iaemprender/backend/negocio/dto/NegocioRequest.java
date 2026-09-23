package com.iaemprender.backend.negocio.dto;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;

public record NegocioRequest(
    @NotBlank String nombre,
    @NotBlank String rubro,
    String ubicacion,
    boolean vendeEnLinea,
    String coberturaEnvio,
    String descripcion,
    String publicoObjetivo,
    String diferenciador,
    String tagline,
    String tono,
    String colores,
    String referenciasEstilo,
    BigDecimal costosFijosMensuales,
    List<String> redesActivas,
    List<String> pilares) {}
