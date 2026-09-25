package com.iaemprender.backend.inteligencia.servicio.cliente;

import com.fasterxml.jackson.annotation.JsonProperty;

public record IniciativaContenido(
    String titulo,
    @JsonProperty("meta_tipo") String metaTipo,
    String pilar,
    String descripcion,
    int impacto,
    int confianza,
    int esfuerzo,
    @JsonProperty("ia_tip") String iaTip) {}
