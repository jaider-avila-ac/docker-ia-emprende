package com.iaemprender.backend.common.excepcion;

import com.iaemprender.backend.common.dto.ErrorResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ManejadorGlobalExcepciones {
  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ErrorResponse> manejarApiException(ApiException ex) {
    return ResponseEntity.status(ex.getEstado())
        .body(ErrorResponse.de(ex.getEstado().value(), ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> manejarValidacion(MethodArgumentNotValidException ex) {
    List<String> detalles = ex.getBindingResult().getFieldErrors().stream()
        .map(e -> e.getField() + ": " + e.getDefaultMessage())
        .toList();
    return ResponseEntity.badRequest()
        .body(ErrorResponse.de(400, "Datos inválidos", detalles));
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponse> manejarAutenticacion(AuthenticationException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ErrorResponse.de(401, "Correo o contraseña incorrectos"));
  }
}
