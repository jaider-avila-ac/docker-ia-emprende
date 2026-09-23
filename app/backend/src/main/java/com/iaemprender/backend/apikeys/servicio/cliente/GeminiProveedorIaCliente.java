package com.iaemprender.backend.apikeys.servicio.cliente;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class GeminiProveedorIaCliente implements ProveedorIaCliente {

  private final RestClient restClient;

  public GeminiProveedorIaCliente(@Value("${app.proveedores-ia.gemini.base-url}") String baseUrl) {
    this.restClient = RestClient.builder().baseUrl(baseUrl).build();
  }

  @Override
  public String getCodigo() {
    return "gemini";
  }

  @Override
  public boolean verificarClave(String claveEnTextoPlano) {
    try {
      restClient.get()
          .uri(uriBuilder -> uriBuilder.path("/v1beta/models").queryParam("key", claveEnTextoPlano).build())
          .retrieve()
          .toBodilessEntity();
      return true;
    } catch (HttpClientErrorException.BadRequest ex) {
      return false;
    } catch (HttpClientErrorException ex) {
      return true;
    } catch (ResourceAccessException ex) {
      throw new VerificacionNoDisponibleException("No se pudo verificar la clave de Gemini en este momento.", ex);
    }
  }
}
