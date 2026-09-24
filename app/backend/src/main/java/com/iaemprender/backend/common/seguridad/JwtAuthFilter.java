package com.iaemprender.backend.common.seguridad;

import com.iaemprender.backend.usuario.repositorio.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final UsuarioRepository usuarioRepository;

  public JwtAuthFilter(JwtService jwtService, UsuarioRepository usuarioRepository) {
    this.jwtService = jwtService;
    this.usuarioRepository = usuarioRepository;
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain) throws ServletException, IOException {
    String header = request.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = header.substring(7);
    if (jwtService.esValido(token)) {
      Long usuarioId = jwtService.extraerUsuarioId(token);
      usuarioRepository.findById(usuarioId).ifPresent(usuario -> {
        UsuarioPrincipal principal = new UsuarioPrincipal(usuario);
        var auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
      });
    }

    filterChain.doFilter(request, response);
  }
}
