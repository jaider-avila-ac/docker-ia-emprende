package com.iaemprender.backend.inteligencia.orquestador.generadores;

import com.iaemprender.backend.common.excepcion.SolicitudInvalidaException;
import com.iaemprender.backend.inteligencia.dto.AnalisisResponse;
import com.iaemprender.backend.inteligencia.orquestador.ContextoIA;
import com.iaemprender.backend.inteligencia.orquestador.GeneradorIA;
import com.iaemprender.backend.inteligencia.orquestador.TipoGeneracion;
import com.iaemprender.backend.inteligencia.servicio.cliente.AnalisisGatewayRespuesta;
import com.iaemprender.backend.inteligencia.servicio.cliente.IaGatewayCliente;
import com.iaemprender.backend.inteligencia.servicio.cliente.IaGatewayNoDisponibleException;
import com.iaemprender.backend.negocio.modelo.Negocio;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AnalisisEvaluacionGenerador implements GeneradorIA<AnalisisGatewayRespuesta, AnalisisResponse> {
  private final IaGatewayCliente iaGatewayCliente;

  public AnalisisEvaluacionGenerador(IaGatewayCliente iaGatewayCliente) {
    this.iaGatewayCliente = iaGatewayCliente;
  }

  @Override
  public TipoGeneracion tipo() {
    return TipoGeneracion.EVALUACION;
  }

  @Override
  public void verificarRequisitos(ContextoIA contexto) {
    if (contexto.datos().aprendizajes().isEmpty() && contexto.datos().resultados().isEmpty()) {
      throw new SolicitudInvalidaException(
          "Registra al menos una evaluación o un resultado semanal para que la IA pueda analizarlos.");
    }
  }

  @Override
  public AnalisisGatewayRespuesta invocar(String proveedor, String clave, ContextoIA contexto) {
    return iaGatewayCliente.analizar(proveedor, clave, contexto.negocio(), contexto.datos());
  }

  @Override
  public AnalisisGatewayRespuesta validar(AnalisisGatewayRespuesta r) {
    if (r == null || vacio(r.resumen()) || vacio(r.siguientePaso())) {
      throw new IaGatewayNoDisponibleException("El análisis devuelto por la IA está incompleto.");
    }
    return r;
  }

  @Override
  public AnalisisResponse guardar(Long usuarioId, Negocio negocio, ContextoIA contexto, AnalisisGatewayRespuesta r) {
    return new AnalisisResponse(
        r.resumen(),
        r.queFunciono() == null ? List.of() : r.queFunciono(),
        r.queCambiar() == null ? List.of() : r.queCambiar(),
        r.siguientePaso());
  }

  private boolean vacio(String texto) {
    return texto == null || texto.isBlank();
  }
}
