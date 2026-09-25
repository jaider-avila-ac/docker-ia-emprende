package com.iaemprender.backend.inteligencia.servicio.cliente;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record BrandingGatewayRespuesta(
    String tagline,
    String tono,
    List<String> pilares,
    String colores,
    @JsonProperty("referencias_estilo") String referenciasEstilo) {}
