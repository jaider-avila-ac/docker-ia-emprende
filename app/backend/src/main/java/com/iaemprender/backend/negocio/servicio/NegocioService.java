package com.iaemprender.backend.negocio.servicio;

import com.iaemprender.backend.negocio.dto.NegocioRequest;
import com.iaemprender.backend.negocio.modelo.Negocio;
import java.util.List;

public interface NegocioService {

  Negocio crear(Long usuarioId, NegocioRequest datos);

  List<Negocio> listarPropios(Long usuarioId);

  Negocio actualizar(Long usuarioId, Long negocioId, NegocioRequest datos);

  Negocio activar(Long usuarioId, Long negocioId);

  Negocio obtenerActivo(Long usuarioId);
}
