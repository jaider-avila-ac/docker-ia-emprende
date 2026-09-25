package com.iaemprender.backend.inteligencia.controlador;

import com.iaemprender.backend.common.seguridad.UsuarioPrincipal;
import com.iaemprender.backend.inteligencia.dto.AnalisisResponse;
import com.iaemprender.backend.inteligencia.servicio.InteligenciaService;
import com.iaemprender.backend.iniciativas.dto.IniciativaResponse;
import com.iaemprender.backend.plan.dto.PlanSemanalResponse;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inteligencia")
public class AsistenteController {
  private final InteligenciaService inteligenciaService;

  public AsistenteController(InteligenciaService inteligenciaService) {
    this.inteligenciaService = inteligenciaService;
  }

  @PostMapping("/iniciativas/generar")
  public List<IniciativaResponse> generarIniciativas(@AuthenticationPrincipal UsuarioPrincipal principal) {
    return inteligenciaService.generarIniciativas(principal.getId());
  }

  @PostMapping("/plan/generar")
  public PlanSemanalResponse generarPlan(@AuthenticationPrincipal UsuarioPrincipal principal) {
    return inteligenciaService.generarPlan(principal.getId());
  }

  @PostMapping("/analisis")
  public AnalisisResponse analizar(@AuthenticationPrincipal UsuarioPrincipal principal) {
    return inteligenciaService.analizar(principal.getId());
  }
}
