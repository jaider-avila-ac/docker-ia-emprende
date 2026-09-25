package com.iaemprender.backend.inteligencia.servicio.impl;

import com.iaemprender.backend.common.excepcion.RecursoNoEncontradoException;
import com.iaemprender.backend.iniciativas.dto.IniciativaResponse;
import com.iaemprender.backend.inteligencia.dto.AnalisisResponse;
import com.iaemprender.backend.inteligencia.dto.FodaItemRequest;
import com.iaemprender.backend.inteligencia.dto.FodaItemResponse;
import com.iaemprender.backend.inteligencia.dto.MetaSmartRequest;
import com.iaemprender.backend.inteligencia.dto.MetaSmartResponse;
import com.iaemprender.backend.inteligencia.modelo.FodaItem;
import com.iaemprender.backend.inteligencia.modelo.MetaSmart;
import com.iaemprender.backend.inteligencia.orquestador.OrquestadorIA;
import com.iaemprender.backend.inteligencia.orquestador.TipoGeneracion;
import com.iaemprender.backend.inteligencia.repositorio.FodaItemRepository;
import com.iaemprender.backend.inteligencia.repositorio.MetaSmartRepository;
import com.iaemprender.backend.inteligencia.servicio.InteligenciaService;
import com.iaemprender.backend.negocio.modelo.Negocio;
import com.iaemprender.backend.negocio.servicio.NegocioService;
import com.iaemprender.backend.plan.dto.PlanSemanalResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InteligenciaServiceImpl implements InteligenciaService {
  private final FodaItemRepository fodaItemRepository;
  private final MetaSmartRepository metaSmartRepository;
  private final NegocioService negocioService;
  private final OrquestadorIA orquestador;

  public InteligenciaServiceImpl(
      FodaItemRepository fodaItemRepository,
      MetaSmartRepository metaSmartRepository,
      NegocioService negocioService,
      OrquestadorIA orquestador) {
    this.fodaItemRepository = fodaItemRepository;
    this.metaSmartRepository = metaSmartRepository;
    this.negocioService = negocioService;
    this.orquestador = orquestador;
  }

  @Override
  public List<FodaItemResponse> generarFoda(Long usuarioId) {
    return orquestador.ejecutar(TipoGeneracion.FODA, usuarioId);
  }

  @Override
  public List<MetaSmartResponse> generarSmart(Long usuarioId) {
    return orquestador.ejecutar(TipoGeneracion.SMART, usuarioId);
  }

  @Override
  public List<IniciativaResponse> generarIniciativas(Long usuarioId) {
    return orquestador.ejecutar(TipoGeneracion.INICIATIVAS, usuarioId);
  }

  @Override
  public PlanSemanalResponse generarPlan(Long usuarioId) {
    return orquestador.ejecutar(TipoGeneracion.PLAN_SEMANAL, usuarioId);
  }

  @Override
  public AnalisisResponse analizar(Long usuarioId) {
    return orquestador.ejecutar(TipoGeneracion.EVALUACION, usuarioId);
  }

  @Override
  public List<FodaItemResponse> listarFoda(Long usuarioId) {
    Negocio negocio = negocioService.obtenerActivo(usuarioId);
    return fodaItemRepository.findByNegocioIdOrderByTipoAscIdAsc(negocio.getId()).stream()
        .map(FodaItemResponse::desde)
        .toList();
  }

  @Override
  public List<MetaSmartResponse> listarSmart(Long usuarioId) {
    Negocio negocio = negocioService.obtenerActivo(usuarioId);
    return metaSmartRepository.findByNegocioIdOrderByIdAsc(negocio.getId()).stream()
        .map(MetaSmartResponse::desde)
        .toList();
  }

  @Override
  @Transactional
  public FodaItemResponse actualizarFodaItem(Long usuarioId, Long itemId, FodaItemRequest datos) {
    FodaItem item = fodaPropioOFallar(usuarioId, itemId);
    item.setContenido(datos.contenido().trim());
    return FodaItemResponse.desde(fodaItemRepository.save(item));
  }

  @Override
  @Transactional
  public void eliminarFodaItem(Long usuarioId, Long itemId) {
    fodaItemRepository.delete(fodaPropioOFallar(usuarioId, itemId));
  }

  @Override
  @Transactional
  public MetaSmartResponse actualizarMeta(Long usuarioId, Long metaId, MetaSmartRequest datos) {
    MetaSmart meta = metaPropiaOFallar(usuarioId, metaId);
    meta.setTitulo(datos.titulo().trim());
    meta.setEspecifico(datos.especifico());
    meta.setNumeroMeta(datos.numeroMeta());
    meta.setFechaLimite(datos.fechaLimite());
    meta.setMedicion(datos.medicion());
    meta.setPasos(datos.pasos());
    return MetaSmartResponse.desde(metaSmartRepository.save(meta));
  }

  @Override
  @Transactional
  public void eliminarMeta(Long usuarioId, Long metaId) {
    metaSmartRepository.delete(metaPropiaOFallar(usuarioId, metaId));
  }

  private FodaItem fodaPropioOFallar(Long usuarioId, Long itemId) {
    Negocio negocio = negocioService.obtenerActivo(usuarioId);
    return fodaItemRepository.findByIdAndNegocioId(itemId, negocio.getId())
        .orElseThrow(() -> new RecursoNoEncontradoException("Elemento del FODA no encontrado."));
  }

  private MetaSmart metaPropiaOFallar(Long usuarioId, Long metaId) {
    Negocio negocio = negocioService.obtenerActivo(usuarioId);
    return metaSmartRepository.findByIdAndNegocioId(metaId, negocio.getId())
        .orElseThrow(() -> new RecursoNoEncontradoException("Meta SMART no encontrada."));
  }
}
