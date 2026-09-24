package com.iaemprender.backend.competidores.controlador;

import com.iaemprender.backend.common.seguridad.UsuarioPrincipal;
import com.iaemprender.backend.competidores.dto.CompetidorRequest;
import com.iaemprender.backend.competidores.dto.CompetidorResponse;
import com.iaemprender.backend.competidores.servicio.CompetidorService;
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
@RequestMapping("/api/competidores")
public class CompetidorController {
  private final CompetidorService competidorService;

  public CompetidorController(CompetidorService competidorService) {
    this.competidorService = competidorService;
  }

  @PostMapping
  public ResponseEntity<CompetidorResponse> crear(
      @AuthenticationPrincipal UsuarioPrincipal principal, @Valid @RequestBody CompetidorRequest datos) {
    var competidor = competidorService.crear(principal.getId(), datos);
    return ResponseEntity.status(HttpStatus.CREATED).body(CompetidorResponse.desde(competidor));
  }

  @GetMapping
  public List<CompetidorResponse> listar(@AuthenticationPrincipal UsuarioPrincipal principal) {
    return competidorService.listarDelNegocioActivo(principal.getId()).stream()
        .map(CompetidorResponse::desde)
        .toList();
  }

  @PutMapping("/{id}")
  public CompetidorResponse actualizar(
      @AuthenticationPrincipal UsuarioPrincipal principal,
      @PathVariable Long id,
      @Valid @RequestBody CompetidorRequest datos) {
    return CompetidorResponse.desde(competidorService.actualizar(principal.getId(), id, datos));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@AuthenticationPrincipal UsuarioPrincipal principal, @PathVariable Long id) {
    competidorService.eliminar(principal.getId(), id);
    return ResponseEntity.noContent().build();
  }
}
