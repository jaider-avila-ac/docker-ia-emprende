package com.iaemprender.backend.inteligencia.dto;

import java.util.List;

public record BrandingSugerenciaResponse(
    String tagline, String tono, List<String> pilares, String colores, String referenciasEstilo) {}
