package com.iaemprender.backend.inteligencia.repositorio;

import com.iaemprender.backend.inteligencia.modelo.MetaSmart;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetaSmartRepository extends JpaRepository<MetaSmart, Long> {
  List<MetaSmart> findByNegocioIdOrderByIdAsc(Long negocioId);

  java.util.Optional<com.iaemprender.backend.inteligencia.modelo.MetaSmart> findByIdAndNegocioId(Long id, Long negocioId);

  void deleteByNegocioId(Long negocioId);
}
