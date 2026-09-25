package com.iaemprender.backend.inteligencia.servicio.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iaemprender.backend.apikeys.dto.ApiKeyEstadoResponse;
import com.iaemprender.backend.apikeys.servicio.ApiKeyService;
import com.iaemprender.backend.common.excepcion.SolicitudInvalidaException;
import com.iaemprender.backend.common.excepcion.RecursoNoEncontradoException;
import com.iaemprender.backend.evaluacion.modelo.Evaluacion;
import com.iaemprender.backend.evaluacion.servicio.EvaluacionService;
import com.iaemprender.backend.inteligencia.dto.AnalisisResponse;
import com.iaemprender.backend.inteligencia.dto.FodaItemRequest;
import com.iaemprender.backend.inteligencia.dto.FodaItemResponse;
import com.iaemprender.backend.inteligencia.dto.MetaSmartRequest;
import com.iaemprender.backend.inteligencia.servicio.cliente.AnalisisGatewayRespuesta;
import com.iaemprender.backend.inteligencia.servicio.cliente.DatosAsistente;
import com.iaemprender.backend.inteligencia.servicio.cliente.IniciativasGatewayRespuesta;
import com.iaemprender.backend.inteligencia.servicio.cliente.PlanGateway;
import com.iaemprender.backend.inteligencia.servicio.cliente.PlanGatewayRespuesta;
import com.iaemprender.backend.iniciativas.dto.IniciativaRequest;
import com.iaemprender.backend.iniciativas.dto.IniciativaResponse;
import com.iaemprender.backend.iniciativas.modelo.EstadoIniciativa;
import com.iaemprender.backend.iniciativas.modelo.Iniciativa;
import com.iaemprender.backend.iniciativas.servicio.IniciativaService;
import com.iaemprender.backend.plan.dto.PlanAccionRequest;
import com.iaemprender.backend.plan.dto.PlanAccionResponse;
import com.iaemprender.backend.plan.dto.PlanSemanalResponse;
import com.iaemprender.backend.plan.modelo.DiaSemana;
import com.iaemprender.backend.plan.modelo.PlanSemanal;
import com.iaemprender.backend.plan.servicio.PlanService;
import com.iaemprender.backend.resultados.modelo.ResultadoSemanal;
import com.iaemprender.backend.resultados.servicio.ResultadoSemanalService;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
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
  private static final List<String> ORDEN_PROVEEDORES = List.of("openai", "gemini", "deepseek");

  private static final String TIPO_FODA = "foda";
  private static final String TIPO_SMART = "smart";

  private final FodaItemRepository fodaItemRepository;
  private final MetaSmartRepository metaSmartRepository;
  private final NegocioIaCacheRepository negocioIaCacheRepository;
  private final NegocioService negocioService;
  private final OfertaService ofertaService;
  private final ApiKeyService apiKeyService;
  private final IaGatewayCliente iaGatewayCliente;
  private final IniciativaService iniciativaService;
  private final PlanService planService;
  private final EvaluacionService evaluacionService;
  private final ResultadoSemanalService resultadoSemanalService;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public InteligenciaServiceImpl(
      FodaItemRepository fodaItemRepository,
      MetaSmartRepository metaSmartRepository,
      NegocioIaCacheRepository negocioIaCacheRepository,
      NegocioService negocioService,
      OfertaService ofertaService,
      ApiKeyService apiKeyService,
      IaGatewayCliente iaGatewayCliente,
      IniciativaService iniciativaService,
      PlanService planService,
      EvaluacionService evaluacionService,
      ResultadoSemanalService resultadoSemanalService) {
    this.iniciativaService = iniciativaService;
    this.planService = planService;
    this.evaluacionService = evaluacionService;
    this.resultadoSemanalService = resultadoSemanalService;
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

  @Override
  @Transactional
  public FodaItemResponse actualizarFodaItem(Long usuarioId, Long itemId, FodaItemRequest datos) {
    Negocio negocio = negocioService.obtenerActivo(usuarioId);
    FodaItem item = fodaItemRepository.findByIdAndNegocioId(itemId, negocio.getId())
        .orElseThrow(() -> new RecursoNoEncontradoException("Elemento del FODA no encontrado."));
    item.setContenido(datos.contenido().trim());
    return FodaItemResponse.desde(fodaItemRepository.save(item));
  }

  @Override
  @Transactional
  public void eliminarFodaItem(Long usuarioId, Long itemId) {
    Negocio negocio = negocioService.obtenerActivo(usuarioId);
    FodaItem item = fodaItemRepository.findByIdAndNegocioId(itemId, negocio.getId())
        .orElseThrow(() -> new RecursoNoEncontradoException("Elemento del FODA no encontrado."));
    fodaItemRepository.delete(item);
  }

  @Override
  @Transactional
  public MetaSmartResponse actualizarMeta(Long usuarioId, Long metaId, MetaSmartRequest datos) {
    Negocio negocio = negocioService.obtenerActivo(usuarioId);
    MetaSmart meta = metaSmartRepository.findByIdAndNegocioId(metaId, negocio.getId())
        .orElseThrow(() -> new RecursoNoEncontradoException("Meta SMART no encontrada."));
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
    Negocio negocio = negocioService.obtenerActivo(usuarioId);
    MetaSmart meta = metaSmartRepository.findByIdAndNegocioId(metaId, negocio.getId())
        .orElseThrow(() -> new RecursoNoEncontradoException("Meta SMART no encontrada."));
    metaSmartRepository.delete(meta);
  }

  @Override
  @Transactional
  public List<IniciativaResponse> generarIniciativas(Long usuarioId) {
    Negocio negocio = negocioService.obtenerActivo(usuarioId);
    var contexto = construirContexto(negocio, usuarioId);
    Map<String, List<String>> foda = fodaPorTipo(negocio.getId());
    List<String> metas = metasComoTexto(negocio.getId());
    if (foda.isEmpty() && metas.isEmpty()) {
      throw new SolicitudInvalidaException(
          "Genera primero tu FODA o tus metas SMART para que la IA pueda proponer iniciativas.");
    }
    List<Iniciativa> existentes = iniciativaService.listarDelNegocioActivo(usuarioId);
    var datos = new DatosAsistente(
        foda,
        metas,
        aprendizajes(usuarioId, existentes),
        resultadosComoTexto(usuarioId),
        existentes.stream().map(Iniciativa::getTitulo).toList(),
        null);

    List<String> proveedores = proveedoresDisponibles(usuarioId);
    IniciativasGatewayRespuesta respuesta = generarConRespaldo(
        usuarioId, proveedores, (proveedor, clave) ->
            iaGatewayCliente.generarIniciativas(proveedor, clave, contexto, datos));

    return respuesta.iniciativas().stream()
        .filter(i -> i.titulo() != null && !i.titulo().isBlank())
        .map(i -> {
          var request = new IniciativaRequest(
              recortar(i.titulo(), 255),
              i.metaTipo(),
              i.pilar(),
              null,
              i.impacto(),
              i.confianza(),
              i.esfuerzo(),
              i.descripcion(),
              null,
              null);
          return IniciativaResponse.desde(iniciativaService.crearGenerada(usuarioId, request, i.iaTip()));
        })
        .toList();
  }

  @Override
  @Transactional
  public PlanSemanalResponse generarPlan(Long usuarioId) {
    Negocio negocio = negocioService.obtenerActivo(usuarioId);
    var contexto = construirContexto(negocio, usuarioId);

    List<Iniciativa> prioritarias = iniciativaService.listarDelNegocioActivo(usuarioId).stream()
        .filter(i -> i.getEstado() != EstadoIniciativa.DESCARTADA)
        .sorted(Comparator.comparingDouble(this::ice).reversed())
        .limit(6)
        .toList();
    if (prioritarias.isEmpty()) {
      throw new SolicitudInvalidaException(
          "Primero genera tus iniciativas: el plan semanal se arma a partir de ellas.");
    }

    PlanSemanal plan = planService.obtenerOCrearSemanaActual(usuarioId);
    var datos = new DatosAsistente(
        Map.of(),
        List.of(),
        aprendizajes(usuarioId, prioritarias),
        List.of(),
        prioritarias.stream().map(this::describirIniciativa).toList(),
        new PlanGateway(
            plan.getTiempoDisponible().getValorDb(),
            plan.getPublicacionesSugeridas() == null ? 0 : plan.getPublicacionesSugeridas(),
            plan.getHistoriasSugeridas() == null ? 0 : plan.getHistoriasSugeridas(),
            plan.getVentanaHoraria() == null ? "" : plan.getVentanaHoraria()));

    List<String> proveedores = proveedoresDisponibles(usuarioId);
    PlanGatewayRespuesta respuesta = generarConRespaldo(
        usuarioId, proveedores, (proveedor, clave) ->
            iaGatewayCliente.generarPlan(proveedor, clave, contexto, datos));

    List<PlanAccionRequest> acciones = respuesta.acciones().stream()
        .filter(a -> a.descripcion() != null && !a.descripcion().isBlank() && diaValido(a.diaSemana()))
        .map(a -> new PlanAccionRequest(a.diaSemana(), recortar(a.descripcion(), 255)))
        .toList();
    if (acciones.isEmpty()) {
      throw new SolicitudInvalidaException("La IA no devolvió un plan utilizable. Inténtalo de nuevo.");
    }

    planService.reemplazarAccionesSemanaActual(usuarioId, acciones);
    var accionesGuardadas =
        planService.listarAcciones(plan.getId()).stream().map(PlanAccionResponse::desde).toList();
    return PlanSemanalResponse.desde(plan, accionesGuardadas);
  }

  @Override
  public AnalisisResponse analizar(Long usuarioId) {
    Negocio negocio = negocioService.obtenerActivo(usuarioId);
    var contexto = construirContexto(negocio, usuarioId);
    List<Iniciativa> iniciativas = iniciativaService.listarDelNegocioActivo(usuarioId);
    List<String> aprendizajes = aprendizajes(usuarioId, iniciativas);
    List<String> resultados = resultadosComoTexto(usuarioId);
    if (aprendizajes.isEmpty() && resultados.isEmpty()) {
      throw new SolicitudInvalidaException(
          "Registra al menos una evaluación o un resultado semanal para que la IA pueda analizarlos.");
    }
    var datos = new DatosAsistente(Map.of(), metasComoTexto(negocio.getId()), aprendizajes, resultados, List.of(), null);

    List<String> proveedores = proveedoresDisponibles(usuarioId);
    AnalisisGatewayRespuesta r = generarConRespaldo(
        usuarioId, proveedores, (proveedor, clave) ->
            iaGatewayCliente.analizar(proveedor, clave, contexto, datos));
    return new AnalisisResponse(
        r.resumen(),
        r.queFunciono() == null ? List.of() : r.queFunciono(),
        r.queCambiar() == null ? List.of() : r.queCambiar(),
        r.siguientePaso());
  }

  private Map<String, List<String>> fodaPorTipo(Long negocioId) {
    Map<String, List<String>> resultado = new LinkedHashMap<>();
    for (FodaItem item : fodaItemRepository.findByNegocioIdOrderByTipoAscIdAsc(negocioId)) {
      String clave = switch (item.getTipo()) {
        case FORTALEZA -> "fortalezas";
        case OPORTUNIDAD -> "oportunidades";
        case DEBILIDAD -> "debilidades";
        case AMENAZA -> "amenazas";
      };
      resultado.computeIfAbsent(clave, k -> new ArrayList<>()).add(item.getContenido());
    }
    return resultado;
  }

  private List<String> metasComoTexto(Long negocioId) {
    return metaSmartRepository.findByNegocioIdOrderByIdAsc(negocioId).stream()
        .map(m -> m.getTitulo() + (m.getNumeroMeta() == null ? "" : " (meta: " + m.getNumeroMeta() + ")"))
        .toList();
  }

  private List<String> aprendizajes(Long usuarioId, List<Iniciativa> iniciativas) {
    Map<Long, String> titulos = iniciativaService.listarDelNegocioActivo(usuarioId).stream()
        .collect(Collectors.toMap(Iniciativa::getId, Iniciativa::getTitulo, (a, b) -> a));
    List<Evaluacion> evaluaciones = evaluacionService.listarDelNegocioActivo(usuarioId);
    return evaluaciones.stream()
        .skip(Math.max(0, evaluaciones.size() - 10))
        .map(e -> {
          String titulo = titulos.getOrDefault(e.getIniciativaId(), "Iniciativa " + e.getIniciativaId());
          String comentario =
              e.getComentarios() == null || e.getComentarios().isBlank() ? "" : ". Comentario: " + e.getComentarios();
          return titulo + ": se logró " + e.getSeLogro().getValorDb() + ", dificultad "
              + e.getDificultad().getValorDb() + ", repetiría " + e.getRepetiria().getValorDb() + comentario;
        })
        .toList();
  }

  private List<String> resultadosComoTexto(Long usuarioId) {
    List<ResultadoSemanal> resultados = resultadoSemanalService.listarDelNegocioActivo(usuarioId);
    return resultados.stream()
        .skip(Math.max(0, resultados.size() - 8))
        .map(r -> "Semana " + r.getSemanaNumero() + "/" + r.getAnio() + ": ingresos aprox. $" + r.getIngresosAprox()
            + (r.getClientesAprox() == null ? "" : ", clientes aprox. " + r.getClientesAprox()))
        .toList();
  }

  private String describirIniciativa(Iniciativa i) {
    String detalle = i.getDescripcion() == null || i.getDescripcion().isBlank() ? "" : ": " + i.getDescripcion();
    return i.getTitulo() + " [" + i.getEstado().getValorDb() + "]" + detalle;
  }

  private double ice(Iniciativa i) {
    return i.getEsfuerzo() == null || i.getEsfuerzo() == 0
        ? 0
        : i.getImpacto() * i.getConfianza() / (double) i.getEsfuerzo();
  }

  private boolean diaValido(String dia) {
    try {
      DiaSemana.desdeValorDb(dia);
      return true;
    } catch (IllegalArgumentException | NullPointerException ex) {
      return false;
    }
  }

  private boolean pilarValido(String pilar) {
    try {
      Pilar.desdeValorDb(pilar);
      return true;
    } catch (IllegalArgumentException | NullPointerException ex) {
      return false;
    }
  }

  private String recortar(String texto, int maximo) {
    if (texto == null) {
      return null;
    }
    String limpio = texto.trim();
    return limpio.length() <= maximo ? limpio : limpio.substring(0, maximo);
  }

  private ContextoNegocioGateway construirContexto(Negocio negocio, Long usuarioId) {
    return new ContextoNegocioGateway(
        negocio.getNombre(),
        negocio.getRubro().getValorDb(),
        negocio.getDescripcion(),
        negocio.getPublicoObjetivo(),
        negocio.getDiferenciador(),
        ofertaService.listarDelNegocioActivo(usuarioId).stream().map(o -> o.getNombre()).toList(),
        negocio.getTono(),
        negocio.getPilares().stream().map(Pilar::getValorDb).toList());
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
          "Configura al menos una clave de IA (OpenAI, Gemini o DeepSeek) en Configuración antes de generar esto.");
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
