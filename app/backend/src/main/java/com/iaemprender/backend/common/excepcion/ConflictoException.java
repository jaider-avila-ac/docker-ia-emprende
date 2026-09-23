package com.iaemprender.backend.common.excepcion;

import org.springframework.http.HttpStatus;

public class ConflictoException extends ApiException {
  public ConflictoException(String mensaje) {
    super(HttpStatus.CONFLICT, mensaje);
  }
}
