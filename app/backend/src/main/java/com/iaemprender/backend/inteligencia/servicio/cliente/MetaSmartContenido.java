package com.iaemprender.backend.inteligencia.servicio.cliente;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public record MetaSmartContenido(
    String titulo,
    String especifico,
    @JsonProperty("numero_meta") String numeroMeta,
    @JsonProperty("fecha_limite") LocalDate fechaLimite,
    String medicion,
    String pasos) {}
