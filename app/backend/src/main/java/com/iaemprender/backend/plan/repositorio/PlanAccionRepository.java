package com.iaemprender.backend.plan.repositorio;

import com.iaemprender.backend.plan.modelo.PlanAccion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanAccionRepository extends JpaRepository<PlanAccion, Long> {
  List<PlanAccion> findByPlanSemanalIdOrderByIdAsc(Long planSemanalId);

  Optional<PlanAccion> findByIdAndPlanSemanal_NegocioId(Long id, Long negocioId);
}
