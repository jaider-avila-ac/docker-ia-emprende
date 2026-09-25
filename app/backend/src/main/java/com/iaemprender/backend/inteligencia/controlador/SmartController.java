package com.iaemprender.backend.inteligencia.controlador;

import com.iaemprender.backend.common.seguridad.UsuarioPrincipal;
import com.iaemprender.backend.inteligencia.dto.MetaSmartResponse;
import com.iaemprender.backend.inteligencia.servicio.InteligenciaService;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.iaemprender.backend.inteligencia.dto.MetaSmartRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inteligencia/smart")
public class SmartController {
  private final InteligenciaService inteligenciaService;

  public SmartController(InteligenciaService inteligenciaService) {
    this.inteligenciaService = inteligenciaService;
  }

  @PostMapping("/generar")
  public List<MetaSmartResponse> generar(@AuthenticationPrincipal UsuarioPrincipal principal) {
    return inteligenciaService.generarSmart(principal.getId());
  }

  @GetMapping
  public List<MetaSmartResponse> listar(@AuthenticationPrincipal UsuarioPrincipal principal) {
    return inteligenciaService.listarSmart(principal.getId());
  }

  @PutMapping("/{id}")
  public MetaSmartResponse actualizar(
      @AuthenticationPrincipal UsuarioPrincipal principal,
      @PathVariable Long id,
      @Valid @RequestBody MetaSmartRequest datos) {
    return inteligenciaService.actualizarMeta(principal.getId(), id, datos);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@AuthenticationPrincipal UsuarioPrincipal principal, @PathVariable Long id) {
    inteligenciaService.eliminarMeta(principal.getId(), id);
    return ResponseEntity.noContent().build();
  }
}
