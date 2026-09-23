package com.iaemprender.backend.inteligencia.servicio.cliente;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SmartGatewayRespuesta(SmartContenido smart, @JsonProperty("tokens_prompt") int tokensPrompt) {}
