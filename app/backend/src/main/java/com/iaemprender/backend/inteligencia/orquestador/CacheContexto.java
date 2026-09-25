package com.iaemprender.backend.inteligencia.orquestador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iaemprender.backend.inteligencia.modelo.NegocioIaCache;
import com.iaemprender.backend.inteligencia.repositorio.NegocioIaCacheRepository;
import com.iaemprender.backend.inteligencia.servicio.cliente.ContextoNegocioGateway;
import com.iaemprender.backend.inteligencia.servicio.cliente.IaGatewayCliente;
import org.springframework.stereotype.Component;

@Component
public class CacheContexto {
  private final NegocioIaCacheRepository repositorio;
  private final IaGatewayCliente iaGatewayCliente;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public CacheContexto(NegocioIaCacheRepository repositorio, IaGatewayCliente iaGatewayCliente) {
    this.repositorio = repositorio;
    this.iaGatewayCliente = iaGatewayCliente;
  }

  public boolean esRedundante(Long negocioId, TipoGeneracion tipo, ContextoNegocioGateway actual) {
    return repositorio.findByNegocioIdAndTipo(negocioId, clave(tipo))
        .map(cache -> {
          ContextoNegocioGateway anterior = leer(cache.getContextoJson());
          return anterior != null && iaGatewayCliente.compararContexto(actual, anterior).similar();
        })
        .orElse(false);
  }

  public void guardar(Long negocioId, TipoGeneracion tipo, ContextoNegocioGateway contexto) {
    String json = escribir(contexto);
    if (json == null) {
      return;
    }
    NegocioIaCache cache = repositorio.findByNegocioIdAndTipo(negocioId, clave(tipo))
        .orElseGet(() -> {
          NegocioIaCache nueva = new NegocioIaCache();
          nueva.setNegocioId(negocioId);
          nueva.setTipo(clave(tipo));
          return nueva;
        });
    cache.setContextoJson(json);
    repositorio.save(cache);
  }

  private String clave(TipoGeneracion tipo) {
    return tipo.name().toLowerCase();
  }

  private String escribir(ContextoNegocioGateway contexto) {
    try {
      return objectMapper.writeValueAsString(contexto);
    } catch (Exception ex) {
      return null;
    }
  }

  private ContextoNegocioGateway leer(String json) {
    try {
      return objectMapper.readValue(json, ContextoNegocioGateway.class);
    } catch (Exception ex) {
      return null;
    }
  }
}
