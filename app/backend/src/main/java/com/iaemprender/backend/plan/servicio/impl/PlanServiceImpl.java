package com.iaemprender.backend.plan.servicio.impl;

import com.iaemprender.backend.common.excepcion.RecursoNoEncontradoException;
import com.iaemprender.backend.common.excepcion.SolicitudInvalidaException;
import com.iaemprender.backend.negocio.modelo.Negocio;
import com.iaemprender.backend.negocio.servicio.NegocioService;
import com.iaemprender.backend.plan.dto.PlanAccionRequest;
import com.iaemprender.backend.plan.modelo.DiaSemana;
import com.iaemprender.backend.plan.modelo.PlanAccion;
import com.iaemprender.backend.plan.modelo.PlanSemanal;
import com.iaemprender.backend.plan.modelo.TiempoDisponible;
import com.iaemprender.backend.plan.repositorio.PlanAccionRepository;
import com.iaemprender.backend.plan.repositorio.PlanSemanalRepository;
import com.iaemprender.backend.plan.servicio.PlanService;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlanServiceImpl implements PlanService {

  private static final Map<TiempoDisponible, SugerenciaCadencia> SUGERENCIAS = Map.of(
      TiempoDisponible.POCO, new SugerenciaCadencia(2, 2, "19:00–20:00"),
      TiempoDisponible.MEDIO, new SugerenciaCadencia(3, 4, "18:00–20:00"),
      TiempoDisponible.BASTANTE, new SugerenciaCadencia(5, 7, "12:00–14:00 y 18:00–20:00"));

  private final PlanSemanalRepository planSemanalRepository;
  private final PlanAccionRepository planAccionRepository;
  private final NegocioService negocioService;

  public PlanServiceImpl(
      PlanSemanalRepository planSemanalRepository,
      PlanAccionRepository planAccionRepository,
      NegocioService negocioService) {
    this.planSemanalRepository = planSemanalRepository;
    this.planAccionRepository = planAccionRepository;
    this.negocioService = negocioService;
  }

  @Override
  @Transactional
  public PlanSemanal obtenerOCrearSemanaActual(Long usuarioId) {
    Negocio negocioActivo = negocioService.obtenerActivo(usuarioId);
    LocalDate hoy = LocalDate.now();
    int anio = hoy.get(WeekFields.ISO.weekBasedYear());
    int semana = hoy.get(WeekFields.ISO.weekOfWeekBasedYear());
    return obtenerOCrear(negocioActivo.getId(), anio, semana);
  }

  @Override
  @Transactional
  public PlanSemanal actualizarAjustes(Long usuarioId, String tiempoDisponible) {
    PlanSemanal plan = obtenerOCrearSemanaActual(usuarioId);
    aplicarTiempoDisponible(plan, tiempoDisponible);
    return planSemanalRepository.save(plan);
  }

  @Override
  @Transactional(readOnly = true)
  public PlanSemanal obtenerSemana(Long usuarioId, int anio, int semanaNumero) {
    Negocio negocioActivo = negocioService.obtenerActivo(usuarioId);
    return planSemanalRepository
        .findByNegocioIdAndAnioAndSemanaNumero(negocioActivo.getId(), anio, semanaNumero)
        .orElseThrow(() -> new RecursoNoEncontradoException("Todavía no hay un plan para esa semana."));
  }

  @Override
  @Transactional
  public PlanSemanal obtenerOCrearSemana(Long usuarioId, int anio, int semanaNumero) {
    Negocio negocioActivo = negocioService.obtenerActivo(usuarioId);
    return obtenerOCrear(negocioActivo.getId(), anio, semanaNumero);
  }

  @Override
  @Transactional(readOnly = true)
  public List<PlanAccion> listarAcciones(Long planSemanalId) {
    return planAccionRepository.findByPlanSemanalIdOrderByIdAsc(planSemanalId);
  }

  @Override
  @Transactional
  public PlanAccion agregarAccion(Long usuarioId, int anio, int semanaNumero, PlanAccionRequest datos) {
    Negocio negocioActivo = negocioService.obtenerActivo(usuarioId);
    PlanSemanal plan = obtenerOCrear(negocioActivo.getId(), anio, semanaNumero);

    PlanAccion accion = new PlanAccion();
    accion.setPlanSemanal(plan);
    aplicarDatosAccion(accion, datos);
    return planAccionRepository.save(accion);
  }

  @Override
  @Transactional
  public PlanAccion actualizarAccion(Long usuarioId, Long accionId, PlanAccionRequest datos) {
    PlanAccion accion = obtenerAccionPropiaOFallar(usuarioId, accionId);
    aplicarDatosAccion(accion, datos);
    return planAccionRepository.save(accion);
  }

  @Override
  @Transactional
  public void eliminarAccion(Long usuarioId, Long accionId) {
    PlanAccion accion = obtenerAccionPropiaOFallar(usuarioId, accionId);
    planAccionRepository.delete(accion);
  }

  private PlanSemanal obtenerOCrear(Long negocioId, int anio, int semanaNumero) {
    return planSemanalRepository.findByNegocioIdAndAnioAndSemanaNumero(negocioId, anio, semanaNumero)
        .orElseGet(() -> {
          PlanSemanal nuevo = new PlanSemanal();
          nuevo.setNegocioId(negocioId);
          nuevo.setAnio(anio);
          nuevo.setSemanaNumero(semanaNumero);
          aplicarTiempoDisponible(nuevo, TiempoDisponible.MEDIO.getValorDb());
          return planSemanalRepository.save(nuevo);
        });
  }

  private void aplicarTiempoDisponible(PlanSemanal plan, String valor) {
    TiempoDisponible tiempo;
    try {
      tiempo = TiempoDisponible.desdeValorDb(valor);
    } catch (IllegalArgumentException ex) {
      throw new SolicitudInvalidaException("Tiempo disponible inválido: " + valor);
    }
    SugerenciaCadencia sugerencia = SUGERENCIAS.get(tiempo);
    plan.setTiempoDisponible(tiempo);
    plan.setPublicacionesSugeridas(sugerencia.publicaciones());
    plan.setHistoriasSugeridas(sugerencia.historias());
    plan.setVentanaHoraria(sugerencia.ventana());
  }

  private PlanAccion obtenerAccionPropiaOFallar(Long usuarioId, Long accionId) {
    Negocio negocioActivo = negocioService.obtenerActivo(usuarioId);
    return planAccionRepository.findByIdAndPlanSemanal_NegocioId(accionId, negocioActivo.getId())
        .orElseThrow(() -> new RecursoNoEncontradoException("Acción no encontrada."));
  }

  private void aplicarDatosAccion(PlanAccion accion, PlanAccionRequest datos) {
    try {
      accion.setDiaSemana(DiaSemana.desdeValorDb(datos.diaSemana()));
    } catch (IllegalArgumentException ex) {
      throw new SolicitudInvalidaException("Día de la semana inválido: " + datos.diaSemana());
    }
    accion.setDescripcion(datos.descripcion());
  }
}
