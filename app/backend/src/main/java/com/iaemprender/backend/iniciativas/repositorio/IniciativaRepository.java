package com.iaemprender.backend.iniciativas.repositorio;

import com.iaemprender.backend.iniciativas.modelo.Iniciativa;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IniciativaRepository extends JpaRepository<Iniciativa, Long> {

  List<Iniciativa> findByNegocioIdOrderByIdAsc(Long negocioId);

  Optional<Iniciativa> findByIdAndNegocioId(Long id, Long negocioId);
}
