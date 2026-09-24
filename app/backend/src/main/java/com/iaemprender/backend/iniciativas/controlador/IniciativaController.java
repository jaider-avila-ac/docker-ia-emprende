package com.iaemprender.backend.iniciativas.controlador;

import com.iaemprender.backend.common.seguridad.UsuarioPrincipal;
import com.iaemprender.backend.iniciativas.dto.IniciativaRequest;
import com.iaemprender.backend.iniciativas.dto.IniciativaResponse;
import com.iaemprender.backend.iniciativas.servicio.IniciativaService;
import jakarta.validation.Valid;
import java.util.Comparator;
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
@RequestMapping("/api/iniciativas")
public class IniciativaController {
  private final IniciativaService iniciativaService;

  public IniciativaController(IniciativaService iniciativaService) {
    this.iniciativaService = iniciativaService;
  }

  @PostMapping
  public ResponseEntity<IniciativaResponse> crear(
      @AuthenticationPrincipal UsuarioPrincipal principal, @Valid @RequestBody IniciativaRequest datos) {
    var iniciativa = iniciativaService.crear(principal.getId(), datos);
    return ResponseEntity.status(HttpStatus.CREATED).body(IniciativaResponse.desde(iniciativa));
  }

  @GetMapping
  public List<IniciativaResponse> listar(@AuthenticationPrincipal UsuarioPrincipal principal) {
    return iniciativaService.listarDelNegocioActivo(principal.getId()).stream()
        .map(IniciativaResponse::desde)
        .sorted(Comparator.comparingDouble(IniciativaResponse::ice).reversed())
        .toList();
  }

  @PutMapping("/{id}")
  public IniciativaResponse actualizar(
      @AuthenticationPrincipal UsuarioPrincipal principal,
      @PathVariable Long id,
      @Valid @RequestBody IniciativaRequest datos) {
    return IniciativaResponse.desde(iniciativaService.actualizar(principal.getId(), id, datos));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@AuthenticationPrincipal UsuarioPrincipal principal, @PathVariable Long id) {
    iniciativaService.eliminar(principal.getId(), id);
    return ResponseEntity.noContent().build();
  }
}
