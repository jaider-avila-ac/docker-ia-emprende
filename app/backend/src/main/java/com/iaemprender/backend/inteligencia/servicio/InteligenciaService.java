package com.iaemprender.backend.inteligencia.servicio;

import com.iaemprender.backend.inteligencia.dto.FodaItemResponse;
import com.iaemprender.backend.inteligencia.dto.MetaSmartResponse;
import java.util.List;

public interface InteligenciaService {
  List<FodaItemResponse> generarFoda(Long usuarioId);

  List<FodaItemResponse> listarFoda(Long usuarioId);

  List<MetaSmartResponse> generarSmart(Long usuarioId);

  List<MetaSmartResponse> listarSmart(Long usuarioId);
}
