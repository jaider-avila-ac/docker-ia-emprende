package com.iaemprender.backend.inteligencia.orquestador;

import com.iaemprender.backend.apikeys.dto.ApiKeyEstadoResponse;
import com.iaemprender.backend.apikeys.servicio.ApiKeyService;
import com.iaemprender.backend.common.excepcion.SolicitudInvalidaException;
import com.iaemprender.backend.inteligencia.servicio.cliente.IaGatewayClaveRechazadaException;
import com.iaemprender.backend.inteligencia.servicio.cliente.IaGatewayNoDisponibleException;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import org.springframework.stereotype.Component;

@Component
public class SelectorProveedorIa {
  private static final List<String> ORDEN_PROVEEDORES = List.of("openai", "gemini", "deepseek");

  private final ApiKeyService apiKeyService;

  public SelectorProveedorIa(ApiKeyService apiKeyService) {
    this.apiKeyService = apiKeyService;
  }

  public <T> T ejecutarConRespaldo(Long usuarioId, BiFunction<String, String, T> llamada) {
    for (String proveedor : proveedoresDisponibles(usuarioId)) {
      String clave = apiKeyService.obtenerClaveDescifrada(usuarioId, proveedor);
      try {
        return llamada.apply(proveedor, clave);
      } catch (IaGatewayClaveRechazadaException | IaGatewayNoDisponibleException ex) {
      }
    }
    throw new SolicitudInvalidaException(
        "No se pudo generar esto con ninguna de tus claves de IA configuradas. Revisa que sigan siendo válidas.");
  }

  public List<String> proveedoresDisponibles(Long usuarioId) {
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
}
