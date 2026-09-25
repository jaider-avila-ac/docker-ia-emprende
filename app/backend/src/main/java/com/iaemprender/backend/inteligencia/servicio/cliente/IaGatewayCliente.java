package com.iaemprender.backend.inteligencia.servicio.cliente;

public interface IaGatewayCliente {
  FodaGatewayRespuesta generarFoda(String proveedor, String claveApi, ContextoNegocioGateway contexto);

  SmartGatewayRespuesta generarSmart(String proveedor, String claveApi, ContextoNegocioGateway contexto);

  IniciativasGatewayRespuesta generarIniciativas(
      String proveedor, String claveApi, ContextoNegocioGateway contexto, DatosAsistente datos);

  PlanGatewayRespuesta generarPlan(String proveedor, String claveApi, ContextoNegocioGateway contexto, DatosAsistente datos);

  AnalisisGatewayRespuesta analizar(String proveedor, String claveApi, ContextoNegocioGateway contexto, DatosAsistente datos);

  ComparacionContextoRespuesta compararContexto(ContextoNegocioGateway actual, ContextoNegocioGateway anterior);
}
