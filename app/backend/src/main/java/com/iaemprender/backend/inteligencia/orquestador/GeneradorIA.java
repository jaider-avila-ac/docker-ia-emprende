package com.iaemprender.backend.inteligencia.orquestador;

import com.iaemprender.backend.negocio.modelo.Negocio;
import java.util.Optional;

public interface GeneradorIA<R, T> {
  TipoGeneracion tipo();

  default void verificarRequisitos(ContextoIA contexto) {}

  default Optional<T> reutilizar(Negocio negocio, ContextoIA contexto) {
    return Optional.empty();
  }

  R invocar(String proveedor, String clave, ContextoIA contexto);

  R validar(R respuesta);

  T guardar(Long usuarioId, Negocio negocio, ContextoIA contexto, R respuesta);
}
