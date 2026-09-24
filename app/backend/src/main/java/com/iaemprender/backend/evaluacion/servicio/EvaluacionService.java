package com.iaemprender.backend.evaluacion.servicio;

import com.iaemprender.backend.evaluacion.dto.EvaluacionRequest;
import com.iaemprender.backend.evaluacion.modelo.Evaluacion;
import java.util.List;

public interface EvaluacionService {
  Evaluacion crear(Long usuarioId, EvaluacionRequest datos);

  List<Evaluacion> listarDelNegocioActivo(Long usuarioId);

  void eliminar(Long usuarioId, Long evaluacionId);
}
