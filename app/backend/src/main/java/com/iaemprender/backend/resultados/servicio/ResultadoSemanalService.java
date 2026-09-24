package com.iaemprender.backend.resultados.servicio;

import com.iaemprender.backend.resultados.dto.ResultadoSemanalRequest;
import com.iaemprender.backend.resultados.modelo.ResultadoSemanal;
import java.util.List;

public interface ResultadoSemanalService {
  ResultadoSemanal crear(Long usuarioId, ResultadoSemanalRequest datos);

  List<ResultadoSemanal> listarDelNegocioActivo(Long usuarioId);

  ResultadoSemanal actualizar(Long usuarioId, Long resultadoId, ResultadoSemanalRequest datos);

  void eliminar(Long usuarioId, Long resultadoId);
}
