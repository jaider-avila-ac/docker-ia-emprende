package com.iaemprender.backend.usuario.servicio;

import com.iaemprender.backend.common.seguridad.UsuarioPrincipal;
import com.iaemprender.backend.usuario.repositorio.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetallesService implements UserDetailsService {
  private final UsuarioRepository usuarioRepository;

  public UsuarioDetallesService(UsuarioRepository usuarioRepository) {
    this.usuarioRepository = usuarioRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String correo) {
    return usuarioRepository.findByCorreo(correo)
        .map(UsuarioPrincipal::new)
        .orElseThrow(() -> new UsernameNotFoundException("Correo o contraseña incorrectos"));
  }
}
