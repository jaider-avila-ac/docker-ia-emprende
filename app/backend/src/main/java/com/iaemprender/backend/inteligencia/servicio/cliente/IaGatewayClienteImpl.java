package com.iaemprender.backend.inteligencia.servicio.cliente;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class IaGatewayClienteImpl implements IaGatewayCliente {
  private final RestClient restClient;

  public IaGatewayClienteImpl(@Value("${app.ia-gateway.base-url}") String baseUrl) {
    var objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    var conversorJson = new MappingJackson2HttpMessageConverter(objectMapper);

    conversorJson.setDefaultCharset(StandardCharsets.UTF_8);

    this.restClient = RestClient.builder()
        .baseUrl(baseUrl)
        .requestFactory(new SimpleClientHttpRequestFactory())
        .messageConverters(converters -> {
          converters.clear();
          converters.add(conversorJson);
        })
        .build();
  }

  private record SolicitudGenerar(
      String proveedor, @JsonProperty("clave_api") String claveApi, ContextoNegocioGateway negocio) {}

  @Override
  public FodaGatewayRespuesta generarFoda(String proveedor, String claveApi, ContextoNegocioGateway contexto) {
    return generar("/foda/generar", proveedor, claveApi, contexto, FodaGatewayRespuesta.class);
  }

  @Override
  public SmartGatewayRespuesta generarSmart(String proveedor, String claveApi, ContextoNegocioGateway contexto) {
    return generar("/smart/generar", proveedor, claveApi, contexto, SmartGatewayRespuesta.class);
  }

  private <T> T generar(
      String ruta, String proveedor, String claveApi, ContextoNegocioGateway contexto, Class<T> tipoRespuesta) {
    try {
      return restClient.post()
          .uri(ruta)
          .body(new SolicitudGenerar(proveedor, claveApi, contexto))
          .retrieve()
          .body(tipoRespuesta);
    } catch (HttpStatusCodeException ex) {
      if (ex.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
        throw new IaGatewayClaveRechazadaException("El proveedor " + proveedor + " rechazó la clave.");
      }
      throw new IaGatewayNoDisponibleException("El servicio de IA no respondió correctamente ahora mismo.");
    } catch (ResourceAccessException ex) {
      throw new IaGatewayNoDisponibleException("No se pudo contactar al servicio de IA ahora mismo.");
    } catch (HttpMessageConversionException ex) {
      throw new IaGatewayNoDisponibleException("El servicio de IA respondió en un formato inesperado.");
    }
  }

  private record SolicitudComparar(ContextoNegocioGateway actual, ContextoNegocioGateway anterior) {}

  @Override
  public ComparacionContextoRespuesta compararContexto(
      ContextoNegocioGateway actual, ContextoNegocioGateway anterior) {
    try {
      return restClient.post()
          .uri("/contexto/comparar")
          .body(new SolicitudComparar(actual, anterior))
          .retrieve()
          .body(ComparacionContextoRespuesta.class);
    } catch (HttpStatusCodeException | ResourceAccessException | HttpMessageConversionException ex) {
      return new ComparacionContextoRespuesta(false, 0.0);
    }
  }
}
