package com.iaemprender.backend.plan.servicio;

import com.iaemprender.backend.plan.dto.PlanAccionRequest;
import com.iaemprender.backend.plan.modelo.PlanAccion;
import com.iaemprender.backend.plan.modelo.PlanSemanal;
import java.util.List;

public interface PlanService {
  PlanSemanal obtenerOCrearSemanaActual(Long usuarioId);

  PlanSemanal actualizarAjustes(Long usuarioId, String tiempoDisponible);

  PlanSemanal obtenerSemana(Long usuarioId, int anio, int semanaNumero);

  PlanSemanal obtenerOCrearSemana(Long usuarioId, int anio, int semanaNumero);

  List<PlanAccion> listarAcciones(Long planSemanalId);

  PlanAccion agregarAccion(Long usuarioId, int anio, int semanaNumero, PlanAccionRequest datos);

  PlanAccion actualizarAccion(Long usuarioId, Long accionId, PlanAccionRequest datos);

  void eliminarAccion(Long usuarioId, Long accionId);
}
