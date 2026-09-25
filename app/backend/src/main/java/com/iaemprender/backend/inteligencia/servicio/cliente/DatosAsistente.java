package com.iaemprender.backend.inteligencia.servicio.cliente;

import java.util.List;
import java.util.Map;

public record DatosAsistente(
    Map<String, List<String>> foda,
    List<String> metas,
    List<String> aprendizajes,
    List<String> resultados,
    List<String> iniciativas,
    PlanGateway plan) {

  public static DatosAsistente vacio() {
    return new DatosAsistente(Map.of(), List.of(), List.of(), List.of(), List.of(), null);
  }
}
