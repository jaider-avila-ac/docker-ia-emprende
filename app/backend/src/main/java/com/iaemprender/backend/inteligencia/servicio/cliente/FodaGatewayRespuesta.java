package com.iaemprender.backend.inteligencia.servicio.cliente;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FodaGatewayRespuesta(FodaContenido foda, @JsonProperty("tokens_prompt") int tokensPrompt) {}
