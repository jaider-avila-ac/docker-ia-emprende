package com.iaemprender.backend.usuario.servicio.impl;

import com.iaemprender.backend.common.excepcion.ConflictoException;
import com.iaemprender.backend.common.excepcion.RecursoNoEncontradoException;
import com.iaemprender.backend.usuario.dto.RegistroRequest;
import com.iaemprender.backend.usuario.modelo.Usuario;
import com.iaemprender.backend.usuario.repositorio.UsuarioRepository;
import com.iaemprender.backend.usuario.servicio.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

  private final UsuarioRepository usuarioRepository;
  private final PasswordEncoder passwordEncoder;

  public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
    this.usuarioRepository = usuarioRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  @Transactional
  public Usuario registrar(RegistroRequest datos) {
    if (usuarioRepository.existsByCorreo(datos.correo())) {
      throw new ConflictoException("Ya existe una cuenta con ese correo.");
    }

    Usuario usuario = new Usuario();
    usuario.setCorreo(datos.correo());

    usuario.setPasswordHash(passwordEncoder.encode(datos.password()));
    usuario.setNombre(datos.nombre());
    usuario.setApellido(datos.apellido());
    usuario.setFechaNacimiento(datos.fechaNacimiento());
    return usuarioRepository.save(usuario);
  }

  @Override
  public Usuario buscarPorId(Long id) {
    return usuarioRepository.findById(id)
        .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado."));
  }
}
