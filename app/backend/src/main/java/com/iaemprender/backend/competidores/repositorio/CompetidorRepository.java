package com.iaemprender.backend.competidores.repositorio;

import com.iaemprender.backend.competidores.modelo.Competidor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetidorRepository extends JpaRepository<Competidor, Long> {
  List<Competidor> findByNegocioIdOrderByIdAsc(Long negocioId);

  Optional<Competidor> findByIdAndNegocioId(Long id, Long negocioId);
}
