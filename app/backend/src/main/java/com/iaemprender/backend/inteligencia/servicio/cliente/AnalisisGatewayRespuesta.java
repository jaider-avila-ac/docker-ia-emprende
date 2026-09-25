package com.iaemprender.backend.inteligencia.servicio.cliente;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record AnalisisGatewayRespuesta(
    String resumen,
    @JsonProperty("que_funciono") List<String> queFunciono,
    @JsonProperty("que_cambiar") List<String> queCambiar,
    @JsonProperty("siguiente_paso") String siguientePaso) {}
