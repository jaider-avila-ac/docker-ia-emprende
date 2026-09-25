package com.iaemprender.backend.inteligencia.repositorio;

import com.iaemprender.backend.inteligencia.modelo.FodaItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FodaItemRepository extends JpaRepository<FodaItem, Long> {
  List<FodaItem> findByNegocioIdOrderByTipoAscIdAsc(Long negocioId);

  java.util.Optional<com.iaemprender.backend.inteligencia.modelo.FodaItem> findByIdAndNegocioId(Long id, Long negocioId);

  void deleteByNegocioId(Long negocioId);
}
