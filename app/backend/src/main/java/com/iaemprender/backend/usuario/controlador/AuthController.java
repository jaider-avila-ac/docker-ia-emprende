package com.iaemprender.backend.usuario.controlador;

import com.iaemprender.backend.common.seguridad.JwtService;
import com.iaemprender.backend.common.seguridad.UsuarioPrincipal;
import com.iaemprender.backend.usuario.dto.LoginRequest;
import com.iaemprender.backend.usuario.dto.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
  }

  @PostMapping("/login")
  public LoginResponse login(@Valid @RequestBody LoginRequest datos) {
    Authentication auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(datos.correo(), datos.password()));
    UsuarioPrincipal principal = (UsuarioPrincipal) auth.getPrincipal();
    String token = jwtService.generarToken(principal.getId(), principal.getUsername());
    return LoginResponse.deToken(token);
  }
}
