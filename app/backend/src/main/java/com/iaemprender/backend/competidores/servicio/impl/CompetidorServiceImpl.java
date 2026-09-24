package com.iaemprender.backend.competidores.servicio.impl;

import com.iaemprender.backend.common.excepcion.RecursoNoEncontradoException;
import com.iaemprender.backend.competidores.dto.CompetidorRequest;
import com.iaemprender.backend.competidores.modelo.Competidor;
import com.iaemprender.backend.competidores.repositorio.CompetidorRepository;
import com.iaemprender.backend.competidores.servicio.CompetidorService;
import com.iaemprender.backend.negocio.modelo.Negocio;
import com.iaemprender.backend.negocio.servicio.NegocioService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompetidorServiceImpl implements CompetidorService {
  private final CompetidorRepository competidorRepository;
  private final NegocioService negocioService;

  public CompetidorServiceImpl(CompetidorRepository competidorRepository, NegocioService negocioService) {
    this.competidorRepository = competidorRepository;
    this.negocioService = negocioService;
  }

  @Override
  @Transactional
  public Competidor crear(Long usuarioId, CompetidorRequest datos) {
    Negocio negocioActivo = negocioService.obtenerActivo(usuarioId);
    Competidor competidor = new Competidor();
    competidor.setNegocioId(negocioActivo.getId());
    aplicarDatos(competidor, datos);
    return competidorRepository.save(competidor);
  }

  @Override
  public List<Competidor> listarDelNegocioActivo(Long usuarioId) {
    Negocio negocioActivo = negocioService.obtenerActivo(usuarioId);
    return competidorRepository.findByNegocioIdOrderByIdAsc(negocioActivo.getId());
  }

  @Override
  @Transactional
  public Competidor actualizar(Long usuarioId, Long competidorId, CompetidorRequest datos) {
    Competidor competidor = obtenerPropioOFallar(usuarioId, competidorId);
    aplicarDatos(competidor, datos);
    return competidorRepository.save(competidor);
  }

  @Override
  @Transactional
  public void eliminar(Long usuarioId, Long competidorId) {
    Competidor competidor = obtenerPropioOFallar(usuarioId, competidorId);
    competidorRepository.delete(competidor);
  }

  private Competidor obtenerPropioOFallar(Long usuarioId, Long competidorId) {
    Negocio negocioActivo = negocioService.obtenerActivo(usuarioId);
    return competidorRepository.findByIdAndNegocioId(competidorId, negocioActivo.getId())
        .orElseThrow(() -> new RecursoNoEncontradoException("Competidor no encontrado."));
  }

  private void aplicarDatos(Competidor competidor, CompetidorRequest datos) {
    competidor.setNombre(datos.nombre());
    competidor.setCanal(datos.canal());
    competidor.setNotas(datos.notas());
  }
}
