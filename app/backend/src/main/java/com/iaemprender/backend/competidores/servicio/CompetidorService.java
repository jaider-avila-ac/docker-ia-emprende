package com.iaemprender.backend.competidores.servicio;

import com.iaemprender.backend.competidores.dto.CompetidorRequest;
import com.iaemprender.backend.competidores.modelo.Competidor;
import java.util.List;

public interface CompetidorService {
  Competidor crear(Long usuarioId, CompetidorRequest datos);

  List<Competidor> listarDelNegocioActivo(Long usuarioId);

  Competidor actualizar(Long usuarioId, Long competidorId, CompetidorRequest datos);

  void eliminar(Long usuarioId, Long competidorId);
}
