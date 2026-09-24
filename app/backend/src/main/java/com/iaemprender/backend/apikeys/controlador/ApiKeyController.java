package com.iaemprender.backend.apikeys.controlador;

import com.iaemprender.backend.apikeys.dto.ApiKeyEstadoResponse;
import com.iaemprender.backend.apikeys.dto.GuardarApiKeyRequest;
import com.iaemprender.backend.apikeys.servicio.ApiKeyService;
import com.iaemprender.backend.common.seguridad.UsuarioPrincipal;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/api-keys")
public class ApiKeyController {
  private final ApiKeyService apiKeyService;

  public ApiKeyController(ApiKeyService apiKeyService) {
    this.apiKeyService = apiKeyService;
  }

  @PutMapping("/{proveedor}")
  public ApiKeyEstadoResponse guardar(
      @AuthenticationPrincipal UsuarioPrincipal principal,
      @PathVariable String proveedor,
      @Valid @RequestBody GuardarApiKeyRequest datos) {
    return apiKeyService.guardar(principal.getId(), proveedor, datos.clave());
  }

  @GetMapping
  public List<ApiKeyEstadoResponse> listar(@AuthenticationPrincipal UsuarioPrincipal principal) {
    return apiKeyService.listarEstados(principal.getId());
  }

  @DeleteMapping("/{proveedor}")
  public ResponseEntity<Void> eliminar(
      @AuthenticationPrincipal UsuarioPrincipal principal, @PathVariable String proveedor) {
    apiKeyService.eliminar(principal.getId(), proveedor);
    return ResponseEntity.noContent().build();
  }
}
