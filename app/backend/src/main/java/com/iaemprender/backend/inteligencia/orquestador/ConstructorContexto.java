package com.iaemprender.backend.inteligencia.orquestador;

import com.iaemprender.backend.evaluacion.modelo.Evaluacion;
import com.iaemprender.backend.evaluacion.servicio.EvaluacionService;
import com.iaemprender.backend.iniciativas.modelo.EstadoIniciativa;
import com.iaemprender.backend.iniciativas.modelo.Iniciativa;
import com.iaemprender.backend.iniciativas.servicio.IniciativaService;
import com.iaemprender.backend.inteligencia.modelo.FodaItem;
import com.iaemprender.backend.inteligencia.repositorio.FodaItemRepository;
import com.iaemprender.backend.inteligencia.repositorio.MetaSmartRepository;
import com.iaemprender.backend.inteligencia.servicio.cliente.ContextoNegocioGateway;
import com.iaemprender.backend.inteligencia.servicio.cliente.DatosAsistente;
import com.iaemprender.backend.inteligencia.servicio.cliente.PlanGateway;
import com.iaemprender.backend.negocio.modelo.Negocio;
import com.iaemprender.backend.negocio.modelo.Pilar;
import com.iaemprender.backend.ofertas.servicio.OfertaService;
import com.iaemprender.backend.plan.modelo.PlanSemanal;
import com.iaemprender.backend.plan.servicio.PlanService;
import com.iaemprender.backend.resultados.modelo.ResultadoSemanal;
import com.iaemprender.backend.resultados.servicio.ResultadoSemanalService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ConstructorContexto {
  private static final int MAX_INICIATIVAS_PLAN = 6;
  private static final int MAX_EVALUACIONES = 10;
  private static final int MAX_RESULTADOS = 8;

  private final OfertaService ofertaService;
  private final FodaItemRepository fodaItemRepository;
  private final MetaSmartRepository metaSmartRepository;
  private final IniciativaService iniciativaService;
  private final EvaluacionService evaluacionService;
  private final ResultadoSemanalService resultadoSemanalService;
  private final PlanService planService;

  public ConstructorContexto(
      OfertaService ofertaService,
      FodaItemRepository fodaItemRepository,
      MetaSmartRepository metaSmartRepository,
      IniciativaService iniciativaService,
      EvaluacionService evaluacionService,
      ResultadoSemanalService resultadoSemanalService,
      PlanService planService) {
    this.ofertaService = ofertaService;
    this.fodaItemRepository = fodaItemRepository;
    this.metaSmartRepository = metaSmartRepository;
    this.iniciativaService = iniciativaService;
    this.evaluacionService = evaluacionService;
    this.resultadoSemanalService = resultadoSemanalService;
    this.planService = planService;
  }

  public ContextoIA construir(TipoGeneracion tipo, Negocio negocio, Long usuarioId) {
    ContextoNegocioGateway base = contextoBase(negocio, usuarioId);
    return switch (tipo) {
      case FODA, SMART -> new ContextoIA(base, DatosAsistente.vacio(), List.of(), null);
      case INICIATIVAS -> paraIniciativas(base, negocio, usuarioId);
      case PLAN_SEMANAL -> paraPlanSemanal(base, usuarioId);
      case EVALUACION -> paraEvaluacion(base, negocio, usuarioId);
    };
  }

  private ContextoIA paraIniciativas(ContextoNegocioGateway base, Negocio negocio, Long usuarioId) {
    List<Iniciativa> existentes = iniciativaService.listarDelNegocioActivo(usuarioId);
    var datos = new DatosAsistente(
        fodaPorTipo(negocio.getId()),
        metasComoTexto(negocio.getId()),
        aprendizajes(usuarioId),
        resultadosComoTexto(usuarioId),
        existentes.stream().map(Iniciativa::getTitulo).toList(),
        null);
    return new ContextoIA(base, datos, existentes, null);
  }

  private ContextoIA paraPlanSemanal(ContextoNegocioGateway base, Long usuarioId) {
    List<Iniciativa> prioritarias = iniciativaService.listarDelNegocioActivo(usuarioId).stream()
        .filter(i -> i.getEstado() != EstadoIniciativa.DESCARTADA)
        .sorted(Comparator.comparingDouble(this::ice).reversed())
        .limit(MAX_INICIATIVAS_PLAN)
        .toList();
    PlanSemanal plan = planService.obtenerOCrearSemanaActual(usuarioId);
    var datos = new DatosAsistente(
        Map.of(),
        List.of(),
        aprendizajes(usuarioId),
        List.of(),
        prioritarias.stream().map(this::describirIniciativa).toList(),
        new PlanGateway(
            plan.getTiempoDisponible().getValorDb(),
            plan.getPublicacionesSugeridas() == null ? 0 : plan.getPublicacionesSugeridas(),
            plan.getHistoriasSugeridas() == null ? 0 : plan.getHistoriasSugeridas(),
            plan.getVentanaHoraria() == null ? "" : plan.getVentanaHoraria()));
    return new ContextoIA(base, datos, prioritarias, plan);
  }

  private ContextoIA paraEvaluacion(ContextoNegocioGateway base, Negocio negocio, Long usuarioId) {
    var datos = new DatosAsistente(
        Map.of(),
        metasComoTexto(negocio.getId()),
        aprendizajes(usuarioId),
        resultadosComoTexto(usuarioId),
        List.of(),
        null);
    return new ContextoIA(base, datos, List.of(), null);
  }

  private ContextoNegocioGateway contextoBase(Negocio negocio, Long usuarioId) {
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

  private List<String> aprendizajes(Long usuarioId) {
    Map<Long, String> titulos = iniciativaService.listarDelNegocioActivo(usuarioId).stream()
        .collect(Collectors.toMap(Iniciativa::getId, Iniciativa::getTitulo, (a, b) -> a));
    List<Evaluacion> evaluaciones = evaluacionService.listarDelNegocioActivo(usuarioId);
    return evaluaciones.stream()
        .skip(Math.max(0, evaluaciones.size() - MAX_EVALUACIONES))
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
        .skip(Math.max(0, resultados.size() - MAX_RESULTADOS))
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
}
