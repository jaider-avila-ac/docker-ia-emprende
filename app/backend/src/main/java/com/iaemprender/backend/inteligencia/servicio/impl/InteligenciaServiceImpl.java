package com.iaemprender.backend.inteligencia.servicio.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iaemprender.backend.apikeys.dto.ApiKeyEstadoResponse;
import com.iaemprender.backend.apikeys.servicio.ApiKeyService;
import com.iaemprender.backend.common.excepcion.SolicitudInvalidaException;
import com.iaemprender.backend.competidores.modelo.Competidor;
import com.iaemprender.backend.competidores.servicio.CompetidorService;
import com.iaemprender.backend.inteligencia.dto.FodaItemResponse;
import com.iaemprender.backend.inteligencia.dto.MetaSmartResponse;
import com.iaemprender.backend.inteligencia.modelo.FodaItem;
import com.iaemprender.backend.inteligencia.modelo.MetaSmart;
import com.iaemprender.backend.inteligencia.modelo.NegocioIaCache;
import com.iaemprender.backend.inteligencia.modelo.TipoFoda;
import com.iaemprender.backend.inteligencia.repositorio.FodaItemRepository;
import com.iaemprender.backend.inteligencia.repositorio.MetaSmartRepository;
import com.iaemprender.backend.inteligencia.repositorio.NegocioIaCacheRepository;
import com.iaemprender.backend.inteligencia.servicio.InteligenciaService;
import com.iaemprender.backend.inteligencia.servicio.cliente.ContextoNegocioGateway;
import com.iaemprender.backend.inteligencia.servicio.cliente.FodaContenido;
import com.iaemprender.backend.inteligencia.servicio.cliente.IaGatewayCliente;
import com.iaemprender.backend.inteligencia.servicio.cliente.IaGatewayClaveRechazadaException;
import com.iaemprender.backend.inteligencia.servicio.cliente.IaGatewayNoDisponibleException;
import com.iaemprender.backend.inteligencia.servicio.cliente.MetaSmartContenido;
import com.iaemprender.backend.negocio.modelo.Negocio;
import com.iaemprender.backend.negocio.modelo.Pilar;
import com.iaemprender.backend.negocio.servicio.NegocioService;
import com.iaemprender.backend.ofertas.servicio.OfertaService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InteligenciaServiceImpl implements InteligenciaService {

  private static final List<String> ORDEN_PROVEEDORES = List.of("openai", "gemini");

  private static final String TIPO_FODA = "foda";
  private static final String TIPO_SMART = "smart";

  private final FodaItemRepository fodaItemRepository;
  private final MetaSmartRepository metaSmartRepository;
  private final NegocioIaCacheRepository negocioIaCacheRepository;
  private final NegocioService negocioService;
  private final OfertaService ofertaService;
  private final CompetidorService competidorService;
  private final ApiKeyService apiKeyService;
  private final IaGatewayCliente iaGatewayCliente;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public InteligenciaServiceImpl(
      FodaItemRepository fodaItemRepository,
      MetaSmartRepository metaSmartRepository,
      NegocioIaCacheRepository negocioIaCacheRepository,
      NegocioService negocioService,
      OfertaService ofertaService,
      CompetidorService competidorService,
      ApiKeyService apiKeyService,
      IaGatewayCliente iaGatewayCliente) {
    this.competidorService = competidorService;
    this.fodaItemRepository = fodaItemRepository;
    this.metaSmartRepository = metaSmartRepository;
    this.negocioIaCacheRepository = negocioIaCacheRepository;
    this.negocioService = negocioService;
    this.ofertaService = ofertaService;
    this.apiKeyService = apiKeyService;
    this.iaGatewayCliente = iaGatewayCliente;
  }

  @Override
  @Transactional
  public List<FodaItemResponse> generarFoda(Long usuarioId) {
    Negocio negocio = negocioService.obtenerActivo(usuarioId);
    var contexto = construirContexto(negocio, usuarioId);

    List<FodaItemResponse> existentes = fodaItemRepository.findByNegocioIdOrderByTipoAscIdAsc(negocio.getId())
        .stream()
        .map(FodaItemResponse::desde)
        .toList();
    if (!existentes.isEmpty() && esRedundante(negocio.getId(), TIPO_FODA, contexto)) {
      return existentes;
    }

    List<String> proveedoresDisponibles = proveedoresDisponibles(usuarioId);
    FodaContenido resultado = generarConRespaldo(
        usuarioId, proveedoresDisponibles, (proveedor, clave) ->
            iaGatewayCliente.generarFoda(proveedor, clave, contexto).foda());

    fodaItemRepository.deleteByNegocioId(negocio.getId());
    List<FodaItem> nuevos = new ArrayList<>();
    agregarItems(nuevos, negocio.getId(), TipoFoda.FORTALEZA, resultado.fortalezas());
    agregarItems(nuevos, negocio.getId(), TipoFoda.OPORTUNIDAD, resultado.oportunidades());
    agregarItems(nuevos, negocio.getId(), TipoFoda.DEBILIDAD, resultado.debilidades());
    agregarItems(nuevos, negocio.getId(), TipoFoda.AMENAZA, resultado.amenazas());
    List<FodaItemResponse> guardados =
        fodaItemRepository.saveAll(nuevos).stream().map(FodaItemResponse::desde).toList();

    guardarContextoCache(negocio.getId(), TIPO_FODA, contexto);
    return guardados;
  }

  @Override
  public List<FodaItemResponse> listarFoda(Long usuarioId) {
    Negocio negocio = negocioService.obtenerActivo(usuarioId);
    return fodaItemRepository.findByNegocioIdOrderByTipoAscIdAsc(negocio.getId()).stream()
        .map(FodaItemResponse::desde)
        .toList();
  }

  @Override
  @Transactional
  public List<MetaSmartResponse> generarSmart(Long usuarioId) {
    Negocio negocio = negocioService.obtenerActivo(usuarioId);
    var contexto = construirContexto(negocio, usuarioId);

    List<MetaSmartResponse> existentes = metaSmartRepository.findByNegocioIdOrderByIdAsc(negocio.getId()).stream()
        .map(MetaSmartResponse::desde)
        .toList();
    if (!existentes.isEmpty() && esRedundante(negocio.getId(), TIPO_SMART, contexto)) {
      return existentes;
    }

    List<String> proveedoresDisponibles = proveedoresDisponibles(usuarioId);
    List<MetaSmartContenido> metas = generarConRespaldo(
        usuarioId, proveedoresDisponibles, (proveedor, clave) ->
            iaGatewayCliente.generarSmart(proveedor, clave, contexto).smart().metas());

    metaSmartRepository.deleteByNegocioId(negocio.getId());
    List<MetaSmart> nuevas = metas.stream().map(m -> {
      MetaSmart meta = new MetaSmart();
      meta.setNegocioId(negocio.getId());
      meta.setTitulo(m.titulo());
      meta.setEspecifico(m.especifico());
      meta.setNumeroMeta(m.numeroMeta());
      meta.setFechaLimite(m.fechaLimite());
      meta.setMedicion(m.medicion());
      meta.setPasos(m.pasos());
      meta.setGeneradoPorIa(true);
      return meta;
    }).toList();
    List<MetaSmartResponse> guardadas =
        metaSmartRepository.saveAll(nuevas).stream().map(MetaSmartResponse::desde).toList();

    guardarContextoCache(negocio.getId(), TIPO_SMART, contexto);
    return guardadas;
  }

  @Override
  public List<MetaSmartResponse> listarSmart(Long usuarioId) {
    Negocio negocio = negocioService.obtenerActivo(usuarioId);
    return metaSmartRepository.findByNegocioIdOrderByIdAsc(negocio.getId()).stream()
        .map(MetaSmartResponse::desde)
        .toList();
  }

  private ContextoNegocioGateway construirContexto(Negocio negocio, Long usuarioId) {
    return new ContextoNegocioGateway(
        negocio.getNombre(),
        negocio.getRubro().getValorDb(),
        negocio.getDescripcion(),
        negocio.getPublicoObjetivo(),
        negocio.getDiferenciador(),
        ofertaService.listarDelNegocioActivo(usuarioId).stream().map(o -> o.getNombre()).toList(),
        negocio.getTagline(),
        negocio.getTono(),
        negocio.getPilares().stream().map(Pilar::getValorDb).toList(),
        competidorService.listarDelNegocioActivo(usuarioId).stream()
            .map(this::describirCompetidor)
            .toList());
  }

  private String describirCompetidor(Competidor c) {
    StringBuilder texto = new StringBuilder(c.getNombre());
    if (c.getCanal() != null && !c.getCanal().isBlank()) {
      texto.append(" (").append(c.getCanal()).append(")");
    }
    if (c.getNotas() != null && !c.getNotas().isBlank()) {
      texto.append(": ").append(c.getNotas());
    }
    return texto.toString();
  }

  private boolean esRedundante(Long negocioId, String tipo, ContextoNegocioGateway actual) {
    return negocioIaCacheRepository.findByNegocioIdAndTipo(negocioId, tipo)
        .map(cache -> {
          ContextoNegocioGateway anterior = deserializarContexto(cache.getContextoJson());
          return anterior != null && iaGatewayCliente.compararContexto(actual, anterior).similar();
        })
        .orElse(false);
  }

  private void guardarContextoCache(Long negocioId, String tipo, ContextoNegocioGateway contexto) {
    String json = serializarContexto(contexto);
    if (json == null) {
      return;
    }
    NegocioIaCache cache = negocioIaCacheRepository.findByNegocioIdAndTipo(negocioId, tipo)
        .orElseGet(() -> {
          NegocioIaCache nueva = new NegocioIaCache();
          nueva.setNegocioId(negocioId);
          nueva.setTipo(tipo);
          return nueva;
        });
    cache.setContextoJson(json);
    negocioIaCacheRepository.save(cache);
  }

  private String serializarContexto(ContextoNegocioGateway contexto) {
    try {
      return objectMapper.writeValueAsString(contexto);
    } catch (Exception ex) {
      return null;
    }
  }

  private ContextoNegocioGateway deserializarContexto(String json) {
    try {
      return objectMapper.readValue(json, ContextoNegocioGateway.class);
    } catch (Exception ex) {
      return null;
    }
  }

  private List<String> proveedoresDisponibles(Long usuarioId) {
    List<String> proveedores = apiKeyService.listarEstados(usuarioId).stream()
        .filter(k -> "activa".equals(k.estado()))
        .map(ApiKeyEstadoResponse::proveedor)
        .filter(ORDEN_PROVEEDORES::contains)
        .sorted(Comparator.comparingInt(ORDEN_PROVEEDORES::indexOf))
        .toList();

    if (proveedores.isEmpty()) {
      throw new SolicitudInvalidaException(
          "Configura al menos una clave de IA (OpenAI o Gemini) en Configuración antes de generar esto.");
    }
    return proveedores;
  }

  private <T> T generarConRespaldo(
      Long usuarioId, List<String> proveedores, BiFunction<String, String, T> generador) {
    for (String proveedor : proveedores) {
      String clave = apiKeyService.obtenerClaveDescifrada(usuarioId, proveedor);
      try {
        return generador.apply(proveedor, clave);
      } catch (IaGatewayClaveRechazadaException | IaGatewayNoDisponibleException ex) {

      }
    }
    throw new SolicitudInvalidaException(
        "No se pudo generar esto con ninguna de tus claves de IA configuradas. Revisa que sigan siendo válidas.");
  }

  private void agregarItems(List<FodaItem> lista, Long negocioId, TipoFoda tipo, List<String> contenidos) {
    for (String contenido : contenidos) {
      FodaItem item = new FodaItem();
      item.setNegocioId(negocioId);
      item.setTipo(tipo);
      item.setContenido(contenido);
      item.setGeneradoPorIa(true);
      lista.add(item);
    }
  }
}
