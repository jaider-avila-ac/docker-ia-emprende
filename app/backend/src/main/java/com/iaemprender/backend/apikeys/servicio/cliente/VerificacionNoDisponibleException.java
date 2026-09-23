package com.iaemprender.backend.apikeys.servicio.cliente;

public class VerificacionNoDisponibleException extends RuntimeException {
  public VerificacionNoDisponibleException(String mensaje, Throwable causa) {
    super(mensaje, causa);
  }
}
