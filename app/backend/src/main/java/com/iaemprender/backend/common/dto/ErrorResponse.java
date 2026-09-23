package com.iaemprender.backend.common.dto;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(Instant momento, int estado, String mensaje, List<String> detalles) {

  public static ErrorResponse de(int estado, String mensaje) {
    return new ErrorResponse(Instant.now(), estado, mensaje, List.of());
  }

  public static ErrorResponse de(int estado, String mensaje, List<String> detalles) {
    return new ErrorResponse(Instant.now(), estado, mensaje, detalles);
  }
}
