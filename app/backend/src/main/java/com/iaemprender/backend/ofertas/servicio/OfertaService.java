package com.iaemprender.backend.ofertas.servicio;

import com.iaemprender.backend.ofertas.dto.OfertaRequest;
import com.iaemprender.backend.ofertas.modelo.Oferta;
import java.util.List;

public interface OfertaService {
  Oferta crear(Long usuarioId, OfertaRequest datos);

  List<Oferta> listarDelNegocioActivo(Long usuarioId);

  Oferta actualizar(Long usuarioId, Long ofertaId, OfertaRequest datos);

  void eliminar(Long usuarioId, Long ofertaId);
}
