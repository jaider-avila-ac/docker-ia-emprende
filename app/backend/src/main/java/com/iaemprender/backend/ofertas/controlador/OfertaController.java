package com.iaemprender.backend.ofertas.controlador;

import com.iaemprender.backend.common.seguridad.UsuarioPrincipal;
import com.iaemprender.backend.ofertas.dto.OfertaRequest;
import com.iaemprender.backend.ofertas.dto.OfertaResponse;
import com.iaemprender.backend.ofertas.servicio.OfertaService;
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
@RequestMapping("/api/ofertas")
public class OfertaController {
  private final OfertaService ofertaService;

  public OfertaController(OfertaService ofertaService) {
    this.ofertaService = ofertaService;
  }

  @PostMapping
  public ResponseEntity<OfertaResponse> crear(
      @AuthenticationPrincipal UsuarioPrincipal principal, @Valid @RequestBody OfertaRequest datos) {
    var oferta = ofertaService.crear(principal.getId(), datos);
    return ResponseEntity.status(HttpStatus.CREATED).body(OfertaResponse.desde(oferta));
  }

  @GetMapping
  public List<OfertaResponse> listar(@AuthenticationPrincipal UsuarioPrincipal principal) {
    return ofertaService.listarDelNegocioActivo(principal.getId()).stream().map(OfertaResponse::desde).toList();
  }

  @PutMapping("/{id}")
  public OfertaResponse actualizar(
      @AuthenticationPrincipal UsuarioPrincipal principal,
      @PathVariable Long id,
      @Valid @RequestBody OfertaRequest datos) {
    return OfertaResponse.desde(ofertaService.actualizar(principal.getId(), id, datos));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@AuthenticationPrincipal UsuarioPrincipal principal, @PathVariable Long id) {
    ofertaService.eliminar(principal.getId(), id);
    return ResponseEntity.noContent().build();
  }
}
