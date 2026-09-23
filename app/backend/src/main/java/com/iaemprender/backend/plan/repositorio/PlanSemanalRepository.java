package com.iaemprender.backend.plan.repositorio;

import com.iaemprender.backend.plan.modelo.PlanSemanal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanSemanalRepository extends JpaRepository<PlanSemanal, Long> {

  Optional<PlanSemanal> findByNegocioIdAndAnioAndSemanaNumero(Long negocioId, Integer anio, Integer semanaNumero);
}
