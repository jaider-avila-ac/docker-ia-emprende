package com.iaemprender.backend.evaluacion.controlador;

import com.iaemprender.backend.common.seguridad.UsuarioPrincipal;
import com.iaemprender.backend.evaluacion.dto.EvaluacionRequest;
import com.iaemprender.backend.evaluacion.dto.EvaluacionResponse;
import com.iaemprender.backend.evaluacion.servicio.EvaluacionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/evaluaciones")
public class EvaluacionController {
  private final EvaluacionService evaluacionService;

  public EvaluacionController(EvaluacionService evaluacionService) {
    this.evaluacionService = evaluacionService;
  }

  @PostMapping
  public ResponseEntity<EvaluacionResponse> crear(
      @AuthenticationPrincipal UsuarioPrincipal principal, @Valid @RequestBody EvaluacionRequest datos) {
    var evaluacion = evaluacionService.crear(principal.getId(), datos);
    return ResponseEntity.status(HttpStatus.CREATED).body(EvaluacionResponse.desde(evaluacion));
  }

  @GetMapping
  public List<EvaluacionResponse> listar(@AuthenticationPrincipal UsuarioPrincipal principal) {
    return evaluacionService.listarDelNegocioActivo(principal.getId()).stream()
        .map(EvaluacionResponse::desde)
        .toList();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@AuthenticationPrincipal UsuarioPrincipal principal, @PathVariable Long id) {
    evaluacionService.eliminar(principal.getId(), id);
    return ResponseEntity.noContent().build();
  }
}
