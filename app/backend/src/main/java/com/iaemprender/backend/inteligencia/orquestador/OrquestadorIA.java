package com.iaemprender.backend.inteligencia.orquestador;

import com.iaemprender.backend.negocio.modelo.Negocio;
import com.iaemprender.backend.negocio.servicio.NegocioService;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrquestadorIA {
  private final Map<TipoGeneracion, GeneradorIA<Object, Object>> generadores = new EnumMap<>(TipoGeneracion.class);
  private final NegocioService negocioService;
  private final ConstructorContexto constructorContexto;
  private final SelectorProveedorIa selectorProveedor;

  @SuppressWarnings("unchecked")
  public OrquestadorIA(
      List<GeneradorIA<?, ?>> generadores,
      NegocioService negocioService,
      ConstructorContexto constructorContexto,
      SelectorProveedorIa selectorProveedor) {
    for (GeneradorIA<?, ?> generador : generadores) {
      this.generadores.put(generador.tipo(), (GeneradorIA<Object, Object>) generador);
    }
    this.negocioService = negocioService;
    this.constructorContexto = constructorContexto;
    this.selectorProveedor = selectorProveedor;
  }

  @Transactional
  @SuppressWarnings("unchecked")
  public <T> T ejecutar(TipoGeneracion tipo, Long usuarioId) {
    GeneradorIA<Object, Object> generador = generadores.get(tipo);
    Negocio negocio = negocioService.obtenerActivo(usuarioId);

    ContextoIA contexto = constructorContexto.construir(tipo, negocio, usuarioId);
    generador.verificarRequisitos(contexto);

    Optional<Object> reutilizado = generador.reutilizar(negocio, contexto);
    if (reutilizado.isPresent()) {
      return (T) reutilizado.get();
    }

    Object respuesta = selectorProveedor.ejecutarConRespaldo(
        usuarioId, (proveedor, clave) -> generador.validar(generador.invocar(proveedor, clave, contexto)));

    return (T) generador.guardar(usuarioId, negocio, contexto, respuesta);
  }
}
