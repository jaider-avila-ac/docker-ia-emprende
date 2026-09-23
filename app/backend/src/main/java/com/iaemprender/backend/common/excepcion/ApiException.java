package com.iaemprender.backend.common.excepcion;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

  private final HttpStatus estado;

  public ApiException(HttpStatus estado, String mensaje) {
    super(mensaje);
    this.estado = estado;
  }

  public HttpStatus getEstado() {
    return estado;
  }
}
