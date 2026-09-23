package com.iaemprender.backend.inteligencia.controlador;

import com.iaemprender.backend.common.seguridad.UsuarioPrincipal;
import com.iaemprender.backend.inteligencia.dto.FodaItemResponse;
import com.iaemprender.backend.inteligencia.servicio.InteligenciaService;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inteligencia/foda")
public class FodaController {

  private final InteligenciaService inteligenciaService;

  public FodaController(InteligenciaService inteligenciaService) {
    this.inteligenciaService = inteligenciaService;
  }

  @PostMapping("/generar")
  public List<FodaItemResponse> generar(@AuthenticationPrincipal UsuarioPrincipal principal) {
    return inteligenciaService.generarFoda(principal.getId());
  }

  @GetMapping
  public List<FodaItemResponse> listar(@AuthenticationPrincipal UsuarioPrincipal principal) {
    return inteligenciaService.listarFoda(principal.getId());
  }
}
