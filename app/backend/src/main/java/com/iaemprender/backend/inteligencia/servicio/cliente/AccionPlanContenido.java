package com.iaemprender.backend.inteligencia.servicio.cliente;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AccionPlanContenido(@JsonProperty("dia_semana") String diaSemana, String descripcion) {}
