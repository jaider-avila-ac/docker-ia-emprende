package com.iaemprender.backend.iniciativas.servicio;

import com.iaemprender.backend.iniciativas.dto.IniciativaRequest;
import com.iaemprender.backend.iniciativas.modelo.Iniciativa;
import java.util.List;

public interface IniciativaService {
  Iniciativa crear(Long usuarioId, IniciativaRequest datos);

  List<Iniciativa> listarDelNegocioActivo(Long usuarioId);

  Iniciativa actualizar(Long usuarioId, Long iniciativaId, IniciativaRequest datos);

  void eliminar(Long usuarioId, Long iniciativaId);

  Iniciativa obtenerPropia(Long usuarioId, Long iniciativaId);
}
