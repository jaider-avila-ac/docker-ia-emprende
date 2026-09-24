package com.iaemprender.backend.inteligencia.servicio.cliente;

public interface IaGatewayCliente {
  FodaGatewayRespuesta generarFoda(String proveedor, String claveApi, ContextoNegocioGateway contexto);

  SmartGatewayRespuesta generarSmart(String proveedor, String claveApi, ContextoNegocioGateway contexto);

  ComparacionContextoRespuesta compararContexto(ContextoNegocioGateway actual, ContextoNegocioGateway anterior);
}
