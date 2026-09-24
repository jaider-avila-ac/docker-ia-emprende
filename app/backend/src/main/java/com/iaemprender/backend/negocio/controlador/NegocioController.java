package com.iaemprender.backend.negocio.controlador;

import com.iaemprender.backend.common.seguridad.UsuarioPrincipal;
import com.iaemprender.backend.negocio.dto.NegocioRequest;
import com.iaemprender.backend.negocio.dto.NegocioResponse;
import com.iaemprender.backend.negocio.modelo.Rubro;
import com.iaemprender.backend.negocio.servicio.NegocioService;
import jakarta.validation.Valid;
import java.util.Arrays;
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
@RequestMapping("/api/negocios")
public class NegocioController {
  private final NegocioService negocioService;

  public NegocioController(NegocioService negocioService) {
    this.negocioService = negocioService;
  }

  @PostMapping
  public ResponseEntity<NegocioResponse> crear(
      @AuthenticationPrincipal UsuarioPrincipal principal, @Valid @RequestBody NegocioRequest datos) {
    var negocio = negocioService.crear(principal.getId(), datos);
    return ResponseEntity.status(HttpStatus.CREATED).body(NegocioResponse.desde(negocio));
  }

  @GetMapping
  public List<NegocioResponse> listarPropios(@AuthenticationPrincipal UsuarioPrincipal principal) {
    return negocioService.listarPropios(principal.getId()).stream().map(NegocioResponse::desde).toList();
  }

  @GetMapping("/activo")
  public NegocioResponse obtenerActivo(@AuthenticationPrincipal UsuarioPrincipal principal) {
    return NegocioResponse.desde(negocioService.obtenerActivo(principal.getId()));
  }

  @PutMapping("/{id}")
  public NegocioResponse actualizar(
      @AuthenticationPrincipal UsuarioPrincipal principal,
      @PathVariable Long id,
      @Valid @RequestBody NegocioRequest datos) {
    return NegocioResponse.desde(negocioService.actualizar(principal.getId(), id, datos));
  }

  @PutMapping("/{id}/activar")
  public NegocioResponse activar(@AuthenticationPrincipal UsuarioPrincipal principal, @PathVariable Long id) {
    return NegocioResponse.desde(negocioService.activar(principal.getId(), id));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@AuthenticationPrincipal UsuarioPrincipal principal, @PathVariable Long id) {
    negocioService.eliminar(principal.getId(), id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/rubros")
  public List<String> rubrosDisponibles() {
    return Arrays.stream(Rubro.values()).map(Rubro::getValorDb).toList();
  }
}
