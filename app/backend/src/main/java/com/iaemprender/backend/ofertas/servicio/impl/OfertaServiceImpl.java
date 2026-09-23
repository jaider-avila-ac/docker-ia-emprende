package com.iaemprender.backend.ofertas.servicio.impl;

import com.iaemprender.backend.common.excepcion.RecursoNoEncontradoException;
import com.iaemprender.backend.common.excepcion.SolicitudInvalidaException;
import com.iaemprender.backend.negocio.modelo.Negocio;
import com.iaemprender.backend.negocio.servicio.NegocioService;
import com.iaemprender.backend.ofertas.dto.OfertaRequest;
import com.iaemprender.backend.ofertas.modelo.Oferta;
import com.iaemprender.backend.ofertas.modelo.TipoOferta;
import com.iaemprender.backend.ofertas.repositorio.OfertaRepository;
import com.iaemprender.backend.ofertas.servicio.OfertaService;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OfertaServiceImpl implements OfertaService {

  private final OfertaRepository ofertaRepository;
  private final NegocioService negocioService;

  public OfertaServiceImpl(OfertaRepository ofertaRepository, NegocioService negocioService) {
    this.ofertaRepository = ofertaRepository;
    this.negocioService = negocioService;
  }

  @Override
  @Transactional
  public Oferta crear(Long usuarioId, OfertaRequest datos) {
    Negocio negocioActivo = negocioService.obtenerActivo(usuarioId);
    Oferta oferta = new Oferta();
    oferta.setNegocioId(negocioActivo.getId());
    aplicarDatos(oferta, datos);
    return ofertaRepository.save(oferta);
  }

  @Override
  public List<Oferta> listarDelNegocioActivo(Long usuarioId) {
    Negocio negocioActivo = negocioService.obtenerActivo(usuarioId);
    return ofertaRepository.findByNegocioIdOrderByIdAsc(negocioActivo.getId());
  }

  @Override
  @Transactional
  public Oferta actualizar(Long usuarioId, Long ofertaId, OfertaRequest datos) {
    Oferta oferta = obtenerPropiaOFallar(usuarioId, ofertaId);
    aplicarDatos(oferta, datos);
    return ofertaRepository.save(oferta);
  }

  @Override
  @Transactional
  public void eliminar(Long usuarioId, Long ofertaId) {
    Oferta oferta = obtenerPropiaOFallar(usuarioId, ofertaId);
    ofertaRepository.delete(oferta);
  }

  private Oferta obtenerPropiaOFallar(Long usuarioId, Long ofertaId) {
    Negocio negocioActivo = negocioService.obtenerActivo(usuarioId);
    return ofertaRepository.findByIdAndNegocioId(ofertaId, negocioActivo.getId())
        .orElseThrow(() -> new RecursoNoEncontradoException("Oferta no encontrada."));
  }

  private void aplicarDatos(Oferta oferta, OfertaRequest datos) {
    try {
      oferta.setTipo(TipoOferta.desdeValorDb(datos.tipo()));
    } catch (IllegalArgumentException ex) {
      throw new SolicitudInvalidaException("Tipo de oferta inválido: " + datos.tipo());
    }
    oferta.setNombre(datos.nombre());
    oferta.setCategoria(datos.categoria());
    oferta.setPrecio(datos.precio());
    oferta.setDestacar(datos.destacar());
  }
}
