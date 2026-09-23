package com.iaemprender.backend.iniciativas.servicio.impl;

import com.iaemprender.backend.common.excepcion.RecursoNoEncontradoException;
import com.iaemprender.backend.common.excepcion.SolicitudInvalidaException;
import com.iaemprender.backend.iniciativas.dto.IniciativaRequest;
import com.iaemprender.backend.iniciativas.modelo.EstadoIniciativa;
import com.iaemprender.backend.iniciativas.modelo.Iniciativa;
import com.iaemprender.backend.iniciativas.repositorio.IniciativaRepository;
import com.iaemprender.backend.iniciativas.servicio.IniciativaService;
import com.iaemprender.backend.negocio.modelo.Negocio;
import com.iaemprender.backend.negocio.servicio.NegocioService;
import com.iaemprender.backend.ofertas.servicio.OfertaService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IniciativaServiceImpl implements IniciativaService {

  private final IniciativaRepository iniciativaRepository;
  private final NegocioService negocioService;
  private final OfertaService ofertaService;

  public IniciativaServiceImpl(
      IniciativaRepository iniciativaRepository, NegocioService negocioService, OfertaService ofertaService) {
    this.iniciativaRepository = iniciativaRepository;
    this.negocioService = negocioService;
    this.ofertaService = ofertaService;
  }

  @Override
  @Transactional
  public Iniciativa crear(Long usuarioId, IniciativaRequest datos) {
    Negocio negocioActivo = negocioService.obtenerActivo(usuarioId);
    Iniciativa iniciativa = new Iniciativa();
    iniciativa.setNegocioId(negocioActivo.getId());
    aplicarDatos(iniciativa, datos, usuarioId);
    return iniciativaRepository.save(iniciativa);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Iniciativa> listarDelNegocioActivo(Long usuarioId) {
    Negocio negocioActivo = negocioService.obtenerActivo(usuarioId);
    return iniciativaRepository.findByNegocioIdOrderByIdAsc(negocioActivo.getId());
  }

  @Override
  @Transactional
  public Iniciativa actualizar(Long usuarioId, Long iniciativaId, IniciativaRequest datos) {
    Iniciativa iniciativa = obtenerPropiaOFallar(usuarioId, iniciativaId);
    aplicarDatos(iniciativa, datos, usuarioId);
    return iniciativaRepository.save(iniciativa);
  }

  @Override
  @Transactional
  public void eliminar(Long usuarioId, Long iniciativaId) {
    Iniciativa iniciativa = obtenerPropiaOFallar(usuarioId, iniciativaId);
    iniciativaRepository.delete(iniciativa);
  }

  @Override
  @Transactional(readOnly = true)
  public Iniciativa obtenerPropia(Long usuarioId, Long iniciativaId) {
    return obtenerPropiaOFallar(usuarioId, iniciativaId);
  }

  private Iniciativa obtenerPropiaOFallar(Long usuarioId, Long iniciativaId) {
    Negocio negocioActivo = negocioService.obtenerActivo(usuarioId);
    return iniciativaRepository.findByIdAndNegocioId(iniciativaId, negocioActivo.getId())
        .orElseThrow(() -> new RecursoNoEncontradoException("Iniciativa no encontrada."));
  }

  private void aplicarDatos(Iniciativa iniciativa, IniciativaRequest datos, Long usuarioId) {
    try {
      iniciativa.setEstado(
          datos.estado() == null || datos.estado().isBlank()
              ? EstadoIniciativa.PROPUESTA
              : EstadoIniciativa.desdeValorDb(datos.estado()));
    } catch (IllegalArgumentException ex) {
      throw new SolicitudInvalidaException("Estado de iniciativa inválido: " + datos.estado());
    }
    iniciativa.setTitulo(datos.titulo());
    iniciativa.setMetaTipo(datos.metaTipo());
    iniciativa.setPilar(datos.pilar());
    iniciativa.setImpacto(datos.impacto());
    iniciativa.setConfianza(datos.confianza());
    iniciativa.setEsfuerzo(datos.esfuerzo());
    iniciativa.setDescripcion(datos.descripcion());
    iniciativa.setNotas(datos.notas());
    iniciativa.setOfertaIds(filtrarOfertasValidas(usuarioId, datos.ofertaIds()));
  }

  private Set<Long> filtrarOfertasValidas(Long usuarioId, List<Long> candidatos) {
    if (candidatos == null || candidatos.isEmpty()) {
      return new HashSet<>();
    }
    Set<Long> propias = ofertaService.listarDelNegocioActivo(usuarioId).stream()
        .map(o -> o.getId())
        .collect(Collectors.toSet());
    Set<Long> validas = new HashSet<>(candidatos);
    validas.retainAll(propias);
    return validas;
  }
}
