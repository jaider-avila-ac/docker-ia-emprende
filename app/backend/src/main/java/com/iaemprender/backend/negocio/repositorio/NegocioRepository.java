package com.iaemprender.backend.negocio.repositorio;

import com.iaemprender.backend.negocio.modelo.Negocio;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NegocioRepository extends JpaRepository<Negocio, Long> {
  List<Negocio> findByUsuarioIdOrderByIdAsc(Long usuarioId);

  long countByUsuarioId(Long usuarioId);

  Optional<Negocio> findByIdAndUsuarioId(Long id, Long usuarioId);

  Optional<Negocio> findByUsuarioIdAndActivoTrue(Long usuarioId);

  @Modifying
  @Query("update Negocio n set n.activo = false where n.usuarioId = :usuarioId")
  void desactivarTodosDe(@Param("usuarioId") Long usuarioId);
}
