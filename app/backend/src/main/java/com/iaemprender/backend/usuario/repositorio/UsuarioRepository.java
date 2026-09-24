package com.iaemprender.backend.usuario.repositorio;

import com.iaemprender.backend.usuario.modelo.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
  Optional<Usuario> findByCorreo(String correo);

  boolean existsByCorreo(String correo);
}
