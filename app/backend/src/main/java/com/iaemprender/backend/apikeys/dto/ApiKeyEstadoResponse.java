package com.iaemprender.backend.apikeys.dto;

import com.iaemprender.backend.apikeys.modelo.UsuarioApiKey;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ApiKeyEstadoResponse(
    String proveedor,
    String nombreProveedor,
    String claveUltimos4,
    String estado,
    long tokensUsadosPeriodo,
    LocalDate periodoInicio,
    LocalDateTime ultimaVerificacion) {
  public static ApiKeyEstadoResponse desde(UsuarioApiKey k) {
    return new ApiKeyEstadoResponse(
        k.getProveedor().getCodigo(),
        k.getProveedor().getNombre(),
        k.getClaveUltimos4(),
        k.getEstado().getValorDb(),
        k.getTokensUsadosPeriodo(),
        k.getPeriodoInicio(),
        k.getUltimaVerificacion());
  }
}
