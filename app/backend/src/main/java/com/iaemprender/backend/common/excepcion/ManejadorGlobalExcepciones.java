package com.iaemprender.backend.common.excepcion;

import com.iaemprender.backend.common.dto.ErrorResponse;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ManejadorGlobalExcepciones {
  private static final Logger registro = LoggerFactory.getLogger(ManejadorGlobalExcepciones.class);

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

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> manejarIntegridad(DataIntegrityViolationException ex) {
    registro.warn("Violación de integridad de datos: {}", ex.getMostSpecificCause().getMessage());
    return ResponseEntity.badRequest()
        .body(ErrorResponse.de(400, "Alguno de los textos es demasiado largo o tiene un valor no permitido."));
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> manejarCuerpoInvalido(HttpMessageNotReadableException ex) {
    return ResponseEntity.badRequest().body(ErrorResponse.de(400, "El contenido enviado no es válido."));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> manejarInesperado(Exception ex) {
    if (ex instanceof org.springframework.web.ErrorResponse respuesta) {
      int estado = respuesta.getStatusCode().value();
      return ResponseEntity.status(estado).body(ErrorResponse.de(estado, "La petición no es válida."));
    }
    registro.error("Error no controlado", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ErrorResponse.de(500, "Ocurrió un error inesperado en el servidor. Inténtalo de nuevo."));
  }
}
