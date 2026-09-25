package com.iaemprender.backend.inteligencia.orquestador.generadores;

import com.iaemprender.backend.inteligencia.dto.FodaItemResponse;
import com.iaemprender.backend.inteligencia.modelo.FodaItem;
import com.iaemprender.backend.inteligencia.modelo.TipoFoda;
import com.iaemprender.backend.inteligencia.orquestador.CacheContexto;
import com.iaemprender.backend.inteligencia.orquestador.ContextoIA;
import com.iaemprender.backend.inteligencia.orquestador.GeneradorIA;
import com.iaemprender.backend.inteligencia.orquestador.TipoGeneracion;
import com.iaemprender.backend.inteligencia.repositorio.FodaItemRepository;
import com.iaemprender.backend.inteligencia.servicio.cliente.FodaContenido;
import com.iaemprender.backend.inteligencia.servicio.cliente.IaGatewayCliente;
import com.iaemprender.backend.inteligencia.servicio.cliente.IaGatewayNoDisponibleException;
import com.iaemprender.backend.negocio.modelo.Negocio;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class FodaGenerador implements GeneradorIA<FodaContenido, List<FodaItemResponse>> {
  private final FodaItemRepository fodaItemRepository;
  private final CacheContexto cacheContexto;
  private final IaGatewayCliente iaGatewayCliente;

  public FodaGenerador(
      FodaItemRepository fodaItemRepository, CacheContexto cacheContexto, IaGatewayCliente iaGatewayCliente) {
    this.fodaItemRepository = fodaItemRepository;
    this.cacheContexto = cacheContexto;
    this.iaGatewayCliente = iaGatewayCliente;
  }

  @Override
  public TipoGeneracion tipo() {
    return TipoGeneracion.FODA;
  }

  @Override
  public Optional<List<FodaItemResponse>> reutilizar(Negocio negocio, ContextoIA contexto) {
    List<FodaItemResponse> existentes = fodaItemRepository.findByNegocioIdOrderByTipoAscIdAsc(negocio.getId())
        .stream()
        .map(FodaItemResponse::desde)
        .toList();
    if (!existentes.isEmpty() && cacheContexto.esRedundante(negocio.getId(), tipo(), contexto.negocio())) {
      return Optional.of(existentes);
    }
    return Optional.empty();
  }

  @Override
  public FodaContenido invocar(String proveedor, String clave, ContextoIA contexto) {
    return iaGatewayCliente.generarFoda(proveedor, clave, contexto.negocio()).foda();
  }

  @Override
  public FodaContenido validar(FodaContenido r) {
    if (r == null || vacia(r.fortalezas()) || vacia(r.oportunidades()) || vacia(r.debilidades())
        || vacia(r.amenazas())) {
      throw new IaGatewayNoDisponibleException("El FODA devuelto por la IA está incompleto.");
    }
    return r;
  }

  @Override
  public List<FodaItemResponse> guardar(Long usuarioId, Negocio negocio, ContextoIA contexto, FodaContenido r) {
    fodaItemRepository.deleteByNegocioId(negocio.getId());
    List<FodaItem> nuevos = new ArrayList<>();
    agregar(nuevos, negocio.getId(), TipoFoda.FORTALEZA, r.fortalezas());
    agregar(nuevos, negocio.getId(), TipoFoda.OPORTUNIDAD, r.oportunidades());
    agregar(nuevos, negocio.getId(), TipoFoda.DEBILIDAD, r.debilidades());
    agregar(nuevos, negocio.getId(), TipoFoda.AMENAZA, r.amenazas());
    List<FodaItemResponse> guardados =
        fodaItemRepository.saveAll(nuevos).stream().map(FodaItemResponse::desde).toList();

    cacheContexto.guardar(negocio.getId(), tipo(), contexto.negocio());
    return guardados;
  }

  private boolean vacia(List<String> lista) {
    return lista == null || lista.isEmpty();
  }

  private void agregar(List<FodaItem> lista, Long negocioId, TipoFoda tipo, List<String> contenidos) {
    for (String contenido : contenidos) {
      FodaItem item = new FodaItem();
      item.setNegocioId(negocioId);
      item.setTipo(tipo);
      item.setContenido(contenido);
      item.setGeneradoPorIa(true);
      lista.add(item);
    }
  }
}
