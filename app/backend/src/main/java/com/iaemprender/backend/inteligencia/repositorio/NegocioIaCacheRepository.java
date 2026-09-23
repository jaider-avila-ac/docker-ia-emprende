package com.iaemprender.backend.inteligencia.repositorio;

import com.iaemprender.backend.inteligencia.modelo.NegocioIaCache;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NegocioIaCacheRepository extends JpaRepository<NegocioIaCache, Long> {

  Optional<NegocioIaCache> findByNegocioIdAndTipo(Long negocioId, String tipo);
}
