package com.iaemprender.backend.evaluacion.repositorio;

import com.iaemprender.backend.evaluacion.modelo.Evaluacion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {
  List<Evaluacion> findByIniciativaIdInOrderByIdDesc(List<Long> iniciativaIds);
}
