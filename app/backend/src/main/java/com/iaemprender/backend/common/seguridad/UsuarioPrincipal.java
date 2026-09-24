package com.iaemprender.backend.common.seguridad;

import com.iaemprender.backend.usuario.modelo.Usuario;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UsuarioPrincipal implements UserDetails {
  private final Usuario usuario;

  public UsuarioPrincipal(Usuario usuario) {
    this.usuario = usuario;
  }

  public Long getId() {
    return usuario.getId();
  }

  public Usuario getUsuario() {
    return usuario;
  }

  @Override
  public String getUsername() {
    return usuario.getCorreo();
  }

  @Override
  public String getPassword() {
    return usuario.getPasswordHash();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of();
  }
}
