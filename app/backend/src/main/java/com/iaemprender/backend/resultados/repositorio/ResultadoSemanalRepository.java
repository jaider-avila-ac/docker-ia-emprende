package com.iaemprender.backend.resultados.repositorio;

import com.iaemprender.backend.resultados.modelo.ResultadoSemanal;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultadoSemanalRepository extends JpaRepository<ResultadoSemanal, Long> {
  List<ResultadoSemanal> findByNegocioIdOrderByAnioAscSemanaNumeroAsc(Long negocioId);

  Optional<ResultadoSemanal> findByIdAndNegocioId(Long id, Long negocioId);

  Optional<ResultadoSemanal> findByNegocioIdAndAnioAndSemanaNumero(Long negocioId, Integer anio, Integer semanaNumero);
}
