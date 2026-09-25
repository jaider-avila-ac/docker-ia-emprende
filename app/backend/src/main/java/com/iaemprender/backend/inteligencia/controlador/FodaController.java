package com.iaemprender.backend.inteligencia.controlador;

import com.iaemprender.backend.common.seguridad.UsuarioPrincipal;
import com.iaemprender.backend.inteligencia.dto.FodaItemResponse;
import com.iaemprender.backend.inteligencia.servicio.InteligenciaService;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.iaemprender.backend.inteligencia.dto.FodaItemRequest;
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
@RequestMapping("/api/inteligencia/foda")
public class FodaController {
  private final InteligenciaService inteligenciaService;

  public FodaController(InteligenciaService inteligenciaService) {
    this.inteligenciaService = inteligenciaService;
  }

  @PostMapping("/generar")
  public List<FodaItemResponse> generar(@AuthenticationPrincipal UsuarioPrincipal principal) {
    return inteligenciaService.generarFoda(principal.getId());
  }

  @GetMapping
  public List<FodaItemResponse> listar(@AuthenticationPrincipal UsuarioPrincipal principal) {
    return inteligenciaService.listarFoda(principal.getId());
  }

  @PutMapping("/{id}")
  public FodaItemResponse actualizar(
      @AuthenticationPrincipal UsuarioPrincipal principal,
      @PathVariable Long id,
      @Valid @RequestBody FodaItemRequest datos) {
    return inteligenciaService.actualizarFodaItem(principal.getId(), id, datos);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> eliminar(@AuthenticationPrincipal UsuarioPrincipal principal, @PathVariable Long id) {
    inteligenciaService.eliminarFodaItem(principal.getId(), id);
    return ResponseEntity.noContent().build();
  }
}
