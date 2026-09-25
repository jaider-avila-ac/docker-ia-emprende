package com.iaemprender.backend.inteligencia.orquestador.generadores;

import com.iaemprender.backend.inteligencia.dto.MetaSmartResponse;
import com.iaemprender.backend.inteligencia.modelo.MetaSmart;
import com.iaemprender.backend.inteligencia.orquestador.CacheContexto;
import com.iaemprender.backend.inteligencia.orquestador.ContextoIA;
import com.iaemprender.backend.inteligencia.orquestador.GeneradorIA;
import com.iaemprender.backend.inteligencia.orquestador.TipoGeneracion;
import com.iaemprender.backend.inteligencia.repositorio.MetaSmartRepository;
import com.iaemprender.backend.inteligencia.servicio.cliente.IaGatewayCliente;
import com.iaemprender.backend.inteligencia.servicio.cliente.IaGatewayNoDisponibleException;
import com.iaemprender.backend.inteligencia.servicio.cliente.MetaSmartContenido;
import com.iaemprender.backend.negocio.modelo.Negocio;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class SmartGenerador implements GeneradorIA<List<MetaSmartContenido>, List<MetaSmartResponse>> {
  private static final int METAS_ESPERADAS = 3;

  private final MetaSmartRepository metaSmartRepository;
  private final CacheContexto cacheContexto;
  private final IaGatewayCliente iaGatewayCliente;

  public SmartGenerador(
      MetaSmartRepository metaSmartRepository, CacheContexto cacheContexto, IaGatewayCliente iaGatewayCliente) {
    this.metaSmartRepository = metaSmartRepository;
    this.cacheContexto = cacheContexto;
    this.iaGatewayCliente = iaGatewayCliente;
  }

  @Override
  public TipoGeneracion tipo() {
    return TipoGeneracion.SMART;
  }

  @Override
  public Optional<List<MetaSmartResponse>> reutilizar(Negocio negocio, ContextoIA contexto) {
    List<MetaSmartResponse> existentes = metaSmartRepository.findByNegocioIdOrderByIdAsc(negocio.getId()).stream()
        .map(MetaSmartResponse::desde)
        .toList();
    if (!existentes.isEmpty() && cacheContexto.esRedundante(negocio.getId(), tipo(), contexto.negocio())) {
      return Optional.of(existentes);
    }
    return Optional.empty();
  }

  @Override
  public List<MetaSmartContenido> invocar(String proveedor, String clave, ContextoIA contexto) {
    return iaGatewayCliente.generarSmart(proveedor, clave, contexto.negocio()).smart().metas();
  }

  @Override
  public List<MetaSmartContenido> validar(List<MetaSmartContenido> metas) {
    if (metas == null || metas.size() < METAS_ESPERADAS) {
      throw new IaGatewayNoDisponibleException("La IA devolvió menos de " + METAS_ESPERADAS + " metas SMART.");
    }
    return metas.subList(0, METAS_ESPERADAS);
  }

  @Override
  public List<MetaSmartResponse> guardar(
      Long usuarioId, Negocio negocio, ContextoIA contexto, List<MetaSmartContenido> metas) {
    metaSmartRepository.deleteByNegocioId(negocio.getId());
    List<MetaSmart> nuevas = metas.stream().map(m -> {
      MetaSmart meta = new MetaSmart();
      meta.setNegocioId(negocio.getId());
      meta.setTitulo(m.titulo());
      meta.setEspecifico(m.especifico());
      meta.setNumeroMeta(m.numeroMeta());
      meta.setFechaLimite(m.fechaLimite());
      meta.setMedicion(m.medicion());
      meta.setPasos(m.pasos());
      meta.setGeneradoPorIa(true);
      return meta;
    }).toList();
    List<MetaSmartResponse> guardadas =
        metaSmartRepository.saveAll(nuevas).stream().map(MetaSmartResponse::desde).toList();

    cacheContexto.guardar(negocio.getId(), tipo(), contexto.negocio());
    return guardadas;
  }
}
