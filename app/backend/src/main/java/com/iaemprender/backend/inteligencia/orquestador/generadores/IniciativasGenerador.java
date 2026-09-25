package com.iaemprender.backend.inteligencia.orquestador.generadores;

import com.iaemprender.backend.common.excepcion.SolicitudInvalidaException;
import com.iaemprender.backend.iniciativas.dto.IniciativaRequest;
import com.iaemprender.backend.iniciativas.dto.IniciativaResponse;
import com.iaemprender.backend.iniciativas.servicio.IniciativaService;
import com.iaemprender.backend.inteligencia.orquestador.ContextoIA;
import com.iaemprender.backend.inteligencia.orquestador.GeneradorIA;
import com.iaemprender.backend.inteligencia.orquestador.TipoGeneracion;
import com.iaemprender.backend.inteligencia.servicio.cliente.IaGatewayCliente;
import com.iaemprender.backend.inteligencia.servicio.cliente.IaGatewayNoDisponibleException;
import com.iaemprender.backend.inteligencia.servicio.cliente.IniciativaContenido;
import com.iaemprender.backend.negocio.modelo.Negocio;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class IniciativasGenerador implements GeneradorIA<List<IniciativaContenido>, List<IniciativaResponse>> {
  private static final int MAXIMO = 5;
  private static final int MINIMO = 3;

  private final IniciativaService iniciativaService;
  private final IaGatewayCliente iaGatewayCliente;

  public IniciativasGenerador(IniciativaService iniciativaService, IaGatewayCliente iaGatewayCliente) {
    this.iniciativaService = iniciativaService;
    this.iaGatewayCliente = iaGatewayCliente;
  }

  @Override
  public TipoGeneracion tipo() {
    return TipoGeneracion.INICIATIVAS;
  }

  @Override
  public void verificarRequisitos(ContextoIA contexto) {
    if (contexto.datos().foda().isEmpty() && contexto.datos().metas().isEmpty()) {
      throw new SolicitudInvalidaException(
          "Genera primero tu FODA o tus metas SMART para que la IA pueda proponer iniciativas.");
    }
  }

  @Override
  public List<IniciativaContenido> invocar(String proveedor, String clave, ContextoIA contexto) {
    return iaGatewayCliente.generarIniciativas(proveedor, clave, contexto.negocio(), contexto.datos()).iniciativas();
  }

  @Override
  public List<IniciativaContenido> validar(List<IniciativaContenido> propuestas) {
    List<IniciativaContenido> validas = propuestas == null
        ? List.of()
        : propuestas.stream()
            .filter(i -> i.titulo() != null && !i.titulo().isBlank())
            .limit(MAXIMO)
            .toList();
    if (validas.size() < MINIMO) {
      throw new IaGatewayNoDisponibleException("La IA devolvió muy pocas iniciativas utilizables.");
    }
    return validas;
  }

  @Override
  public List<IniciativaResponse> guardar(
      Long usuarioId, Negocio negocio, ContextoIA contexto, List<IniciativaContenido> propuestas) {
    return propuestas.stream()
        .map(i -> {
          var solicitud = new IniciativaRequest(
              i.titulo().trim(),
              i.metaTipo(),
              i.pilar(),
              null,
              i.impacto(),
              i.confianza(),
              i.esfuerzo(),
              i.descripcion(),
              null,
              null);
          return IniciativaResponse.desde(iniciativaService.crearGenerada(usuarioId, solicitud, i.iaTip()));
        })
        .toList();
  }
}
