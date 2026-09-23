package com.iaemprender.backend.usuario.dto;

import com.iaemprender.backend.usuario.modelo.Usuario;
import java.time.LocalDate;

public record UsuarioResponse(
    Long id, String correo, String nombre, String apellido, LocalDate fechaNacimiento) {

  public static UsuarioResponse desde(Usuario usuario) {
    return new UsuarioResponse(
        usuario.getId(),
        usuario.getCorreo(),
        usuario.getNombre(),
        usuario.getApellido(),
        usuario.getFechaNacimiento());
  }
}
