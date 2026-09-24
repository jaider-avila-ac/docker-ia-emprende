package com.iaemprender.backend.evaluacion.servicio.impl;

import com.iaemprender.backend.common.excepcion.RecursoNoEncontradoException;
import com.iaemprender.backend.common.excepcion.SolicitudInvalidaException;
import com.iaemprender.backend.evaluacion.dto.EvaluacionRequest;
import com.iaemprender.backend.evaluacion.modelo.Dificultad;
import com.iaemprender.backend.evaluacion.modelo.Evaluacion;
import com.iaemprender.backend.evaluacion.modelo.Repetiria;
import com.iaemprender.backend.evaluacion.modelo.SeLogro;
import com.iaemprender.backend.evaluacion.repositorio.EvaluacionRepository;
import com.iaemprender.backend.evaluacion.servicio.EvaluacionService;
import com.iaemprender.backend.iniciativas.servicio.IniciativaService;
import com.iaemprender.backend.plan.servicio.PlanService;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EvaluacionServiceImpl implements EvaluacionService {
  private final EvaluacionRepository evaluacionRepository;
  private final IniciativaService iniciativaService;
  private final PlanService planService;

  public EvaluacionServiceImpl(
      EvaluacionRepository evaluacionRepository, IniciativaService iniciativaService, PlanService planService) {
    this.evaluacionRepository = evaluacionRepository;
    this.iniciativaService = iniciativaService;
    this.planService = planService;
  }

  @Override
  @Transactional
  public Evaluacion crear(Long usuarioId, EvaluacionRequest datos) {
    var iniciativa = iniciativaService.obtenerPropia(usuarioId, datos.iniciativaId());

    Evaluacion evaluacion = new Evaluacion();
    evaluacion.setIniciativaId(iniciativa.getId());
    if (datos.semanaNumero() != null) {
      int anio = LocalDate.now().get(WeekFields.ISO.weekBasedYear());
      var planSemanal = planService.obtenerOCrearSemana(usuarioId, anio, datos.semanaNumero());
      evaluacion.setPlanSemanalId(planSemanal.getId());
    }

    try {
      evaluacion.setSeLogro(SeLogro.desdeValorDb(datos.seLogro()));
      evaluacion.setDificultad(Dificultad.desdeValorDb(datos.dificultad()));
      evaluacion.setRepetiria(Repetiria.desdeValorDb(datos.repetiria()));
    } catch (IllegalArgumentException ex) {
      throw new SolicitudInvalidaException(ex.getMessage());
    }
    evaluacion.setComentarios(datos.comentarios());

    return evaluacionRepository.save(evaluacion);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Evaluacion> listarDelNegocioActivo(Long usuarioId) {
    List<Long> iniciativaIds = iniciativaService.listarDelNegocioActivo(usuarioId).stream()
        .map(i -> i.getId())
        .toList();
    if (iniciativaIds.isEmpty()) {
      return List.of();
    }
    return evaluacionRepository.findByIniciativaIdInOrderByIdDesc(iniciativaIds);
  }

  @Override
  @Transactional
  public void eliminar(Long usuarioId, Long evaluacionId) {
    Evaluacion evaluacion = evaluacionRepository.findById(evaluacionId)
        .orElseThrow(() -> new RecursoNoEncontradoException("Evaluación no encontrada."));

    iniciativaService.obtenerPropia(usuarioId, evaluacion.getIniciativaId());
    evaluacionRepository.delete(evaluacion);
  }
}
