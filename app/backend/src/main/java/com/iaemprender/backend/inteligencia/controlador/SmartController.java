package com.iaemprender.backend.inteligencia.controlador;

import com.iaemprender.backend.common.seguridad.UsuarioPrincipal;
import com.iaemprender.backend.inteligencia.dto.MetaSmartResponse;
import com.iaemprender.backend.inteligencia.servicio.InteligenciaService;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inteligencia/smart")
public class SmartController {

  private final InteligenciaService inteligenciaService;

  public SmartController(InteligenciaService inteligenciaService) {
    this.inteligenciaService = inteligenciaService;
  }

  @PostMapping("/generar")
  public List<MetaSmartResponse> generar(@AuthenticationPrincipal UsuarioPrincipal principal) {
    return inteligenciaService.generarSmart(principal.getId());
  }

  @GetMapping
  public List<MetaSmartResponse> listar(@AuthenticationPrincipal UsuarioPrincipal principal) {
    return inteligenciaService.listarSmart(principal.getId());
  }
}
