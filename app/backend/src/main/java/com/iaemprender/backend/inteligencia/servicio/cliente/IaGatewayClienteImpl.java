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

  private record SolicitudAsistente(
      String proveedor,
      @JsonProperty("clave_api") String claveApi,
      ContextoNegocioGateway negocio,
      java.util.Map<String, java.util.List<String>> foda,
      java.util.List<String> metas,
      java.util.List<String> aprendizajes,
      java.util.List<String> resultados,
      java.util.List<String> iniciativas,
      PlanGateway plan) {}

  @Override
  public IniciativasGatewayRespuesta generarIniciativas(
      String proveedor, String claveApi, ContextoNegocioGateway contexto, DatosAsistente datos) {
    return asistente("/asistente/iniciativas", proveedor, claveApi, contexto, datos, IniciativasGatewayRespuesta.class);
  }

  @Override
  public PlanGatewayRespuesta generarPlan(
      String proveedor, String claveApi, ContextoNegocioGateway contexto, DatosAsistente datos) {
    return asistente("/asistente/plan", proveedor, claveApi, contexto, datos, PlanGatewayRespuesta.class);
  }

  @Override
  public AnalisisGatewayRespuesta analizar(
      String proveedor, String claveApi, ContextoNegocioGateway contexto, DatosAsistente datos) {
    return asistente("/asistente/analisis", proveedor, claveApi, contexto, datos, AnalisisGatewayRespuesta.class);
  }

  private <T> T asistente(
      String ruta,
      String proveedor,
      String claveApi,
      ContextoNegocioGateway contexto,
      DatosAsistente datos,
      Class<T> tipoRespuesta) {
    var solicitud = new SolicitudAsistente(
        proveedor,
        claveApi,
        contexto,
        datos.foda(),
        datos.metas(),
        datos.aprendizajes(),
        datos.resultados(),
        datos.iniciativas(),
        datos.plan());
    return ejecutar(ruta, proveedor, solicitud, tipoRespuesta);
  }

  private <T> T generar(
      String ruta, String proveedor, String claveApi, ContextoNegocioGateway contexto, Class<T> tipoRespuesta) {
    return ejecutar(ruta, proveedor, new SolicitudGenerar(proveedor, claveApi, contexto), tipoRespuesta);
  }

  private <T> T ejecutar(String ruta, String proveedor, Object cuerpo, Class<T> tipoRespuesta) {
    try {
      return restClient.post()
          .uri(ruta)
          .body(cuerpo)
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
