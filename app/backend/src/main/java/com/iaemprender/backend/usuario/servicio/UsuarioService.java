package com.iaemprender.backend.usuario.servicio;

import com.iaemprender.backend.usuario.dto.RegistroRequest;
import com.iaemprender.backend.usuario.modelo.Usuario;

public interface UsuarioService {
  Usuario registrar(RegistroRequest datos);

  Usuario buscarPorId(Long id);
}
