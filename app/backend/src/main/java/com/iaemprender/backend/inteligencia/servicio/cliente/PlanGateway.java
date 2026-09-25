package com.iaemprender.backend.inteligencia.servicio.cliente;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PlanGateway(
    @JsonProperty("tiempo_disponible") String tiempoDisponible, int publicaciones, int historias, String ventana) {}
