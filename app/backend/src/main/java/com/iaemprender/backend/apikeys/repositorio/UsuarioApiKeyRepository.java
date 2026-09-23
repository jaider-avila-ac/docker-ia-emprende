package com.iaemprender.backend.apikeys.repositorio;

import com.iaemprender.backend.apikeys.modelo.UsuarioApiKey;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioApiKeyRepository extends JpaRepository<UsuarioApiKey, Long> {

  List<UsuarioApiKey> findByUsuarioId(Long usuarioId);

  Optional<UsuarioApiKey> findByUsuarioIdAndProveedor_Codigo(Long usuarioId, String codigoProveedor);
}
