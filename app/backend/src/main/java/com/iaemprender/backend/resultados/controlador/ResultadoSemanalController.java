package com.iaemprender.backend.resultados.controlador;

import com.iaemprender.backend.common.seguridad.UsuarioPrincipal;
import com.iaemprender.backend.resultados.dto.ResultadoSemanalRequest;
import com.iaemprender.backend.resultados.dto.ResultadoSemanalResponse;
import com.iaemprender.backend.resultados.servicio.ResultadoSemanalService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/resultados")
public class ResultadoSemanalController {

  private final ResultadoSemanalService resultadoSemanalService;

  public ResultadoSemanalController(ResultadoSemanalService resultadoSemanalService) {
    this.resultadoSemanalService = resultadoSemanalService;
  }

  @PostMapping
  public ResponseEntity<ResultadoSemanalResponse> crear(
      @AuthenticationPrincipal UsuarioPrincipal principal, @Valid @RequestBody ResultadoSemanalRequest datos) {
    var resultado = resultadoSemanalService.crear(principal.getId(), datos);
    return ResponseEntity.status(HttpStatus.CREATED).body(ResultadoSemanalResponse.desde(resultado));
  }

  @GetMapping
  public List<ResultadoSemanalResponse> listar(@AuthenticationPrincipal UsuarioPrincipal principal) {
    return resultadoSemanalService.listarDelNegocioActivo(principal.getId()).stream()
        .map(ResultadoSemanalResponse::desde)
        .toList();
  }

  @PutMapping("/{id}")
  public ResultadoSemanalResponse actualizar(
      @AuthenticationPrincipal UsuarioPrincipal principal,
      @PathVariable Long id,
      @Valid @RequestBody ResultadoSemanalRequest datos) {
    return ResultadoSemanalResponse.desde(resultadoSemanalService.actualizar(principal.getId(), id, datos));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@AuthenticationPrincipal UsuarioPrincipal principal, @PathVariable Long id) {
    resultadoSemanalService.eliminar(principal.getId(), id);
    return ResponseEntity.noContent().build();
  }
}
