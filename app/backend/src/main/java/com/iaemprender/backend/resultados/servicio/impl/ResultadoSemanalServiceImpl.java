package com.iaemprender.backend.resultados.servicio.impl;

import com.iaemprender.backend.common.excepcion.ConflictoException;
import com.iaemprender.backend.common.excepcion.RecursoNoEncontradoException;
import com.iaemprender.backend.negocio.modelo.Negocio;
import com.iaemprender.backend.negocio.servicio.NegocioService;
import com.iaemprender.backend.resultados.dto.ResultadoSemanalRequest;
import com.iaemprender.backend.resultados.modelo.ResultadoSemanal;
import com.iaemprender.backend.resultados.repositorio.ResultadoSemanalRepository;
import com.iaemprender.backend.resultados.servicio.ResultadoSemanalService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResultadoSemanalServiceImpl implements ResultadoSemanalService {

  private final ResultadoSemanalRepository resultadoSemanalRepository;
  private final NegocioService negocioService;

  public ResultadoSemanalServiceImpl(
      ResultadoSemanalRepository resultadoSemanalRepository, NegocioService negocioService) {
    this.resultadoSemanalRepository = resultadoSemanalRepository;
    this.negocioService = negocioService;
  }

  @Override
  @Transactional
  public ResultadoSemanal crear(Long usuarioId, ResultadoSemanalRequest datos) {
    Negocio negocioActivo = negocioService.obtenerActivo(usuarioId);
    verificarSinDuplicado(negocioActivo.getId(), datos, null);

    ResultadoSemanal resultado = new ResultadoSemanal();
    resultado.setNegocioId(negocioActivo.getId());
    aplicarDatos(resultado, datos);
    return resultadoSemanalRepository.save(resultado);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ResultadoSemanal> listarDelNegocioActivo(Long usuarioId) {
    Negocio negocioActivo = negocioService.obtenerActivo(usuarioId);
    return resultadoSemanalRepository.findByNegocioIdOrderByAnioAscSemanaNumeroAsc(negocioActivo.getId());
  }

  @Override
  @Transactional
  public ResultadoSemanal actualizar(Long usuarioId, Long resultadoId, ResultadoSemanalRequest datos) {
    ResultadoSemanal resultado = obtenerPropioOFallar(usuarioId, resultadoId);
    verificarSinDuplicado(resultado.getNegocioId(), datos, resultadoId);
    aplicarDatos(resultado, datos);
    return resultadoSemanalRepository.save(resultado);
  }

  @Override
  @Transactional
  public void eliminar(Long usuarioId, Long resultadoId) {
    ResultadoSemanal resultado = obtenerPropioOFallar(usuarioId, resultadoId);
    resultadoSemanalRepository.delete(resultado);
  }

  private ResultadoSemanal obtenerPropioOFallar(Long usuarioId, Long resultadoId) {
    Negocio negocioActivo = negocioService.obtenerActivo(usuarioId);
    return resultadoSemanalRepository.findByIdAndNegocioId(resultadoId, negocioActivo.getId())
        .orElseThrow(() -> new RecursoNoEncontradoException("Resultado no encontrado."));
  }

  private void verificarSinDuplicado(Long negocioId, ResultadoSemanalRequest datos, Long idAIgnorar) {
    resultadoSemanalRepository
        .findByNegocioIdAndAnioAndSemanaNumero(negocioId, datos.anio(), datos.semanaNumero())
        .filter(existente -> !existente.getId().equals(idAIgnorar))
        .ifPresent(existente -> {
          throw new ConflictoException(
              "Ya registraste un resultado para la semana " + datos.semanaNumero() + " de " + datos.anio()
                  + " — edítalo o bórralo en vez de duplicarlo.");
        });
  }

  private void aplicarDatos(ResultadoSemanal resultado, ResultadoSemanalRequest datos) {
    resultado.setAnio(datos.anio());
    resultado.setSemanaNumero(datos.semanaNumero());
    resultado.setIngresosAprox(datos.ingresosAprox());
    resultado.setClientesAprox(datos.clientesAprox());
  }
}
