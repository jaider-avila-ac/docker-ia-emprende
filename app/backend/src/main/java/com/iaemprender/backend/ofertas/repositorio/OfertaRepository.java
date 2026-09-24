package com.iaemprender.backend.ofertas.repositorio;

import com.iaemprender.backend.ofertas.modelo.Oferta;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfertaRepository extends JpaRepository<Oferta, Long> {
  List<Oferta> findByNegocioIdOrderByIdAsc(Long negocioId);

  Optional<Oferta> findByIdAndNegocioId(Long id, Long negocioId);
}
