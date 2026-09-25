package com.iaemprender.backend.inteligencia.orquestador.generadores;

import com.iaemprender.backend.common.excepcion.SolicitudInvalidaException;
import com.iaemprender.backend.inteligencia.orquestador.ContextoIA;
import com.iaemprender.backend.inteligencia.orquestador.GeneradorIA;
import com.iaemprender.backend.inteligencia.orquestador.TipoGeneracion;
import com.iaemprender.backend.inteligencia.servicio.cliente.AccionPlanContenido;
import com.iaemprender.backend.inteligencia.servicio.cliente.IaGatewayCliente;
import com.iaemprender.backend.inteligencia.servicio.cliente.IaGatewayNoDisponibleException;
import com.iaemprender.backend.negocio.modelo.Negocio;
import com.iaemprender.backend.plan.dto.PlanAccionRequest;
import com.iaemprender.backend.plan.dto.PlanAccionResponse;
import com.iaemprender.backend.plan.dto.PlanSemanalResponse;
import com.iaemprender.backend.plan.modelo.DiaSemana;
import com.iaemprender.backend.plan.servicio.PlanService;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PlanSemanalGenerador implements GeneradorIA<List<AccionPlanContenido>, PlanSemanalResponse> {
  private static final int MAXIMO_ACCIONES = 10;

  private final PlanService planService;
  private final IaGatewayCliente iaGatewayCliente;

  public PlanSemanalGenerador(PlanService planService, IaGatewayCliente iaGatewayCliente) {
    this.planService = planService;
    this.iaGatewayCliente = iaGatewayCliente;
  }

  @Override
  public TipoGeneracion tipo() {
    return TipoGeneracion.PLAN_SEMANAL;
  }

  @Override
  public void verificarRequisitos(ContextoIA contexto) {
    if (contexto.iniciativas().isEmpty()) {
      throw new SolicitudInvalidaException("Primero genera tus iniciativas: el plan semanal se arma a partir de ellas.");
    }
  }

  @Override
  public List<AccionPlanContenido> invocar(String proveedor, String clave, ContextoIA contexto) {
    return iaGatewayCliente.generarPlan(proveedor, clave, contexto.negocio(), contexto.datos()).acciones();
  }

  @Override
  public List<AccionPlanContenido> validar(List<AccionPlanContenido> acciones) {
    List<AccionPlanContenido> validas = acciones == null
        ? List.of()
        : acciones.stream()
            .filter(a -> a.descripcion() != null && !a.descripcion().isBlank() && diaValido(a.diaSemana()))
            .limit(MAXIMO_ACCIONES)
            .toList();
    if (validas.isEmpty()) {
      throw new IaGatewayNoDisponibleException("La IA no devolvió un plan utilizable.");
    }
    return validas;
  }

  @Override
  public PlanSemanalResponse guardar(
      Long usuarioId, Negocio negocio, ContextoIA contexto, List<AccionPlanContenido> acciones) {
    List<PlanAccionRequest> solicitudes = acciones.stream()
        .map(a -> new PlanAccionRequest(a.diaSemana(), recortar(a.descripcion(), 255)))
        .toList();
    planService.reemplazarAccionesSemanaActual(usuarioId, solicitudes);

    var guardadas =
        planService.listarAcciones(contexto.plan().getId()).stream().map(PlanAccionResponse::desde).toList();
    return PlanSemanalResponse.desde(contexto.plan(), guardadas);
  }

  private boolean diaValido(String dia) {
    try {
      DiaSemana.desdeValorDb(dia);
      return true;
    } catch (IllegalArgumentException | NullPointerException ex) {
      return false;
    }
  }

  private String recortar(String texto, int maximo) {
    String limpio = texto.trim();
    return limpio.length() <= maximo ? limpio : limpio.substring(0, maximo);
  }
}
