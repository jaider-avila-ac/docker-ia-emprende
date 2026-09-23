package com.iaemprender.backend.usuario.controlador;

import com.iaemprender.backend.common.seguridad.UsuarioPrincipal;
import com.iaemprender.backend.usuario.dto.RegistroRequest;
import com.iaemprender.backend.usuario.dto.UsuarioResponse;
import com.iaemprender.backend.usuario.modelo.Usuario;
import com.iaemprender.backend.usuario.servicio.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

  private final UsuarioService usuarioService;

  public UsuarioController(UsuarioService usuarioService) {
    this.usuarioService = usuarioService;
  }

  @PostMapping
  public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroRequest datos) {
    Usuario usuario = usuarioService.registrar(datos);
    return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponse.desde(usuario));
  }

  @GetMapping("/yo")
  public UsuarioResponse yo(@AuthenticationPrincipal UsuarioPrincipal principal) {
    return UsuarioResponse.desde(principal.getUsuario());
  }
}
