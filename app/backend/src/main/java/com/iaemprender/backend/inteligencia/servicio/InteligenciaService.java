package com.iaemprender.backend.inteligencia.servicio;

import com.iaemprender.backend.inteligencia.dto.FodaItemResponse;
import com.iaemprender.backend.inteligencia.dto.MetaSmartResponse;
import com.iaemprender.backend.inteligencia.dto.AnalisisResponse;
import com.iaemprender.backend.inteligencia.dto.BrandingSugerenciaResponse;
import com.iaemprender.backend.inteligencia.dto.FodaItemRequest;
import com.iaemprender.backend.inteligencia.dto.MetaSmartRequest;
import com.iaemprender.backend.iniciativas.dto.IniciativaResponse;
import com.iaemprender.backend.plan.dto.PlanSemanalResponse;
import java.util.List;

public interface InteligenciaService {
  List<FodaItemResponse> generarFoda(Long usuarioId);

  List<FodaItemResponse> listarFoda(Long usuarioId);

  List<MetaSmartResponse> generarSmart(Long usuarioId);

  List<MetaSmartResponse> listarSmart(Long usuarioId);

  FodaItemResponse actualizarFodaItem(Long usuarioId, Long itemId, FodaItemRequest datos);

  void eliminarFodaItem(Long usuarioId, Long itemId);

  MetaSmartResponse actualizarMeta(Long usuarioId, Long metaId, MetaSmartRequest datos);

  void eliminarMeta(Long usuarioId, Long metaId);

  List<IniciativaResponse> generarIniciativas(Long usuarioId);

  PlanSemanalResponse generarPlan(Long usuarioId);

  BrandingSugerenciaResponse sugerirBranding(Long usuarioId);

  AnalisisResponse analizar(Long usuarioId);
}
