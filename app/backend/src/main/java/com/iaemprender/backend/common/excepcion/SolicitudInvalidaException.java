package com.iaemprender.backend.common.excepcion;

import org.springframework.http.HttpStatus;

public class SolicitudInvalidaException extends ApiException {
  public SolicitudInvalidaException(String mensaje) {
    super(HttpStatus.BAD_REQUEST, mensaje);
  }
}
