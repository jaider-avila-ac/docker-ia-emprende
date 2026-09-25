package com.iaemprender.backend.apikeys.servicio.cliente;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class DeepSeekProveedorIaCliente implements ProveedorIaCliente {
  private final RestClient restClient;

  public DeepSeekProveedorIaCliente(@Value("${app.proveedores-ia.deepseek.base-url}") String baseUrl) {
    this.restClient = RestClient.builder().baseUrl(baseUrl).build();
  }

  @Override
  public String getCodigo() {
    return "deepseek";
  }

  @Override
  public boolean verificarClave(String claveEnTextoPlano) {
    try {
      restClient.get()
          .uri("/models")
          .header(HttpHeaders.AUTHORIZATION, "Bearer " + claveEnTextoPlano)
          .retrieve()
          .toBodilessEntity();
      return true;
    } catch (HttpClientErrorException.Unauthorized ex) {
      return false;
    } catch (HttpClientErrorException ex) {
      return true;
    } catch (ResourceAccessException ex) {
      throw new VerificacionNoDisponibleException("No se pudo verificar la clave de DeepSeek en este momento.", ex);
    }
  }
}
