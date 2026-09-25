package com.iaemprender.backend.inteligencia.servicio.cliente;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ContextoNegocioGateway(
    String nombre,
    String rubro,
    String descripcion,
    @JsonProperty("publico_objetivo") String publicoObjetivo,
    String diferenciador,
    List<String> ofertas,
    String tono,
    List<String> pilares) {}
